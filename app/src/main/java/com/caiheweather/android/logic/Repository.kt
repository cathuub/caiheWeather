package com.caiheweather.android.logic


import androidx.lifecycle.liveData
import com.caiheweather.android.logic.dao.PicDao
import com.caiheweather.android.logic.dao.PlaceDao
import com.caiheweather.android.logic.model.Place
import com.caiheweather.android.logic.model.Weather
import com.caiheweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        //搜寻地点数据方法，调用网络专用类SunnyWeatherNetwork专用函数
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.code == "200") {//获取到信息，注意这里的location是城市列表
            val places = placeResponse.location
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.code}"))
        }
    }

    fun refreshWeather(lng: String, lat: String, placeName: String) = fire(Dispatchers.IO) {
        //搜寻天气数据方法
        coroutineScope {
            val point="$lng,$lat"
            //获取天气实时数据
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(point)
            }
            //获取天气预报数据
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(point)
            }
            //获取空气质量
            val deferredair = async {
                SunnyWeatherNetwork.getnowair(point)
            }
            //获取生活指数
            val deferredindices = async {
                SunnyWeatherNetwork.getindices(point)
            }

            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            val airResponse = deferredair.await()
            val indicesResponse = deferredindices.await()

            if (realtimeResponse.code == "200" && dailyResponse.code == "200" && airResponse.code == "200" && indicesResponse.code == "200") {
                //将数据录入到weather对象内，需要空气质量，逐天天气，天气预报，风力风向数据
                val weather = Weather(realtimeResponse.now, dailyResponse.daily,airResponse.now,indicesResponse.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.code}" +
                                "daily response status is ${dailyResponse.code}"
                    )
                )
            }
        }
    }

    //需要新建方法导出生活指数？？

    //需要新建方法和类修改整个前缀导出历史数据？？

    //下面是缓存天气数据代码块，可以考虑加入到候选列表
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
    //
    fun savePic(uri: String) = PicDao.savePic(uri)

    fun getSavedPic() = PicDao.getSavedPlace()

    fun isPicSaved() = PicDao.isPlaceSaved()
}