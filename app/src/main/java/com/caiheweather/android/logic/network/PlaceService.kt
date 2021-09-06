package com.caiheweather.android.logic.network

import com.caiheweather.android.SunnyWeatherApplication
import com.caiheweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("v2/city/lookup?key=${SunnyWeatherApplication.Key}")
    fun searchPlaces(@Query("location") query: String): Call<PlaceResponse>

}