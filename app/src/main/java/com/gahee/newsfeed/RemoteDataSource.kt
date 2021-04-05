package com.gahee.newsfeed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


class NewsViewModel : BaseViewModel() {
    private val service = NewsService.create(Utils.HANKYUNG_BASE_URL)

    private var _channelResponse: MutableLiveData<ChannelResponse> = MutableLiveData()
    val channelInfo: LiveData<ChannelResponse> = _channelResponse

    fun fetchNewsByCategory(category: String) {
        val dis = service.fetchNewsByCategory(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorReturn {
                    RssResponse()
                }
                .doOnError {
                    Log.e(TAG, "fetchNewsByCategory: ${it.localizedMessage}")
                }
                .subscribe {
                    _channelResponse.postValue(it.channel)
                }
        add(dis)
    }

    companion object {
        const val TAG = "NewsViewModel"
    }
}

@Suppress("UNCHECKED_CAST")
class NewsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel() as T
    }
}

open class BaseViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

class NewsService {

    companion object {
        private lateinit var instance: NewsApi

        fun create(baseUrl: String): NewsApi {
            if (!this::instance.isInitialized) {
                instance =
                        Retrofit.Builder()
                                .baseUrl(baseUrl)
                                .callFactory(OkHttpClient.Builder().build())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(SimpleXmlConverterFactory.create())
                                .build().create(NewsApi::class.java)
            }
            return instance
        }
    }

}

interface NewsApi {

    @GET("{category}")
    fun fetchNewsByCategory(@Path("category") category: String): Observable<RssResponse>

}
