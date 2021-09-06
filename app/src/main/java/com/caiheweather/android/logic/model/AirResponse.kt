package com.caiheweather.android.logic.model

class AirResponse (val code: String, val now: Nowair){

    class Nowair(
            val aqi:String,
            val category:String,
            val primary:String,
            val pm10:String,
            val pm2p5:String,
            val no2:String,
            val so2:String,
            val co:String,
            val o3:String
    )
}