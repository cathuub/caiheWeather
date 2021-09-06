package com.caiheweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreatorofplace {

    private const val BASE_URL = "https://geoapi.qweather.com/"

    private val retrofit = Retrofit.Builder()//创造连接，使用基本url
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}
object ServiceCreatorofweather {

    private const val BASE_URL = "https://devapi.qweather.com/"

    private val retrofit = Retrofit.Builder()//创造连接，使用基本url
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}