package com.caiheweather.android.logic.model



class RealtimeResponse(val code: String, val now: Now) {

    class Now(val temp:String,
              val feelsLike:String,
              val icon:String,
              val text:String,
              val humidity:String,
              val precip:String,
              val pressure:String,
              val vis:String,
              val cloud:String,
              val wind360:String,
              val windDir:String,
              val windScale:String,
              val windSpeed:String
              )
}