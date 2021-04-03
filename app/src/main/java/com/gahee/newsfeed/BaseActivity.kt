package com.gahee.newsfeed

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor

abstract class BaseActivity : AppCompatActivity() {
    lateinit var publishProcessor: PublishProcessor<Boolean>
    lateinit var disposable: Disposable
    lateinit var broadCastReceiver: BroadcastReceiver

    override fun onStart() {
        super.onStart()
        publishProcessor = PublishProcessor.create()
        disposable = publishProcessor
            .startWith(getConnectivityStatus(this))
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isOnline ->
                if (isOnline) onOnline()
                else onOffline()
            }
        listenToNetworkConnectivity()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
        unregisterReceiver(broadCastReceiver)
    }

    private fun listenToNetworkConnectivity(){
        broadCastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                publishProcessor.onNext(context?.let { getConnectivityStatus(it) })
            }
        }

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(broadCastReceiver, intentFilter)
    }

    private fun getConnectivityStatus(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected
    }

    abstract fun onOnline()
    abstract fun onOffline()

    companion object {
        private const val TAG = "BaseActivity"
    }
}