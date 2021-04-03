package com.gahee.newsfeed

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.gahee.cnn.databinding.ActivityMainBinding
import com.gahee.newsfeed.Utils.Companion as Hankyung

class MainActivity : BaseActivity(){

    lateinit var newsViewModel: NewsViewModel
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        newsViewModel = ViewModelProvider(this, NewsViewModelFactory())[NewsViewModel::class.java]

        newsViewModel.newsItems.observe(this, { list ->
            list?.let {
                binding.mainViewPager.adapter = PagerRecyclerAdapter(it, this.supportFragmentManager)
            }
        })

        newsViewModel.channelInfo.observe(this, { channel ->
            channel?.copyright?.let {
                binding.mainFooter.text = it
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.main_news_menu -> {
                newsViewModel.fetchNewsByCategory(Hankyung.NEWS_MAIN)
                true
            }
            R.id.stock_news_menu -> {
                newsViewModel.fetchNewsByCategory(Hankyung.NEWS_STOCK)
                true
            }
            R.id.society_news_menu -> {
                newsViewModel.fetchNewsByCategory(Hankyung.NEWS_SOCIETY)
                true
            }
            else -> false
        }
    }

    override fun onOnline() {
        binding.mainViewPager.visibility = View.VISIBLE
        binding.mainFooter.visibility = View.VISIBLE
        binding.checkNetworkView.visibility = View.GONE

        newsViewModel.fetchNewsByCategory(Hankyung.NEWS_STOCK)
    }

    override fun onOffline() {
        binding.mainViewPager.visibility = View.GONE
        binding.mainFooter.visibility = View.GONE
        binding.checkNetworkView.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}