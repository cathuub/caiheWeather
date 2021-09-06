package com.caiheweather.android.logic.network

import com.caiheweather.android.SunnyWeatherApplication
import com.caiheweather.android.logic.model.AirResponse
import com.caiheweather.android.logic.model.DailyResponse
import com.caiheweather.android.logic.model.IndicesResponse
import com.caiheweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {


    //当前天气指数
    @GET("v7/weather/now?key=${SunnyWeatherApplication.Key}")//当前实时天气消息，具有空气指标，和风天气需要另外添加
    fun getRealtimeWeather(@Query("location") point: String): Call<RealtimeResponse>//此处 有& 因此不能使用Path。。。。
    //未来预报（三天）
    @GET("v7/weather/7d?key=${SunnyWeatherApplication.Key}")
    fun getDailyWeather(@Query("location") point: String): Call<DailyResponse>

    //逐小时预报
    @GET("v7/weather/24h?key=${SunnyWeatherApplication.Key}")
    fun gettiemWeather(@Query("location") point: String): Call<DailyResponse>

    //空气质量
    @GET("v7/air/now?key=${SunnyWeatherApplication.Key}")
    fun getnowair(@Query("location") point: String): Call<AirResponse>
    
    //生活质量
    @GET("v7/indices/1d?type=1,2,3,9&key=${SunnyWeatherApplication.Key}")
    fun getindices(@Query("location") point: String): Call<IndicesResponse>

}