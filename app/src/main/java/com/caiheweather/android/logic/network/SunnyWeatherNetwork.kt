package com.caiheweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {

    private val weatherService = ServiceCreatorofweather.create(WeatherService::class.java)


    //下面是刷新天气信息四个函数
    suspend fun getDailyWeather(point: String) = weatherService.getDailyWeather(point).await()
    suspend fun getRealtimeWeather(point: String) = weatherService.getRealtimeWeather(point).await()
    //空气信息
    suspend fun getnowair(point: String) = weatherService.getnowair(point).await()
    //生活指数
    suspend fun getindices(point: String) = weatherService.getindices(point).await()

    private val placeService = ServiceCreatorofplace.create(PlaceService::class.java)
    //下面是获取地点信息的一个函数
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()


    //下面是两者公用的网络传输报文函数
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    //测试点一，测试网络请求是否符合规则
                    val body = response.body()

                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}