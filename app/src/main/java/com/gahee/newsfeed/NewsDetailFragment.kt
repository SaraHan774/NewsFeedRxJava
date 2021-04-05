package com.gahee.newsfeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gahee.newsfeed.databinding.NewsDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection
import java.net.URL

class NewsDetailFragment : BottomSheetDialogFragment{

    lateinit var binding : NewsDetailBinding
    lateinit var link: String

    constructor()

    constructor(link: String){
        this.link = link
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewsDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection

        val obs = Observable.create<String>{
            try {
                val text = urlConnection.inputStream.bufferedReader().readText()
                it.onNext(text)
            } catch (e : Exception){
                it.onError(e)
            } finally {
                urlConnection.disconnect()
                it.onComplete()
            }
        }

        obs.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { "" }
            .subscribe {
                binding.newsDetailText.text = it
            }
    }

}