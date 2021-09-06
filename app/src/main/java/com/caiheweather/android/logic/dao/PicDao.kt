package com.caiheweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.caiheweather.android.SunnyWeatherApplication

object PicDao {

    //存储是pic路径！！！
    fun savePic(uri:String) {
        sharedPreferences().edit {
            putString("uri",uri)
        }
    }

    fun getSavedPlace(): String? {
        val placeJson = sharedPreferences().getString("uri", "")
        return placeJson
    }

    fun isPlaceSaved() = sharedPreferences().contains("uri")

    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}