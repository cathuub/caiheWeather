package com.caiheweather.android.logic.model

class Weather(val realtime: RealtimeResponse.Now,
              val daily: List<DailyResponse.Daily>,
              val air:AirResponse.Nowair,
              val indices: List<IndicesResponse.dailyIndices>)