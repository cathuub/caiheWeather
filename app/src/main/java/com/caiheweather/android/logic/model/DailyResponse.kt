package com.caiheweather.android.logic.model

//逐天天气预报类，7天版
class DailyResponse(val code: String, val daily:List<Daily>) {

    class Daily(
                val fxDate:String,
                val sunrise:String,
                val sunset:String,
                val moonrise:String,
                val moonset:String,
                val moonPhase:String,
                val tempMax:String,
                val tempMin:String,
                val iconDay:String,
                val textDay:String,
                val iconNight:String,
                val textNight:String,
                val humidity:String,
                val precip:String,
                val pressure:String,
                val vis:String,
                val cloud:String,
                val uvIndex:String//紫外线度
                )



}