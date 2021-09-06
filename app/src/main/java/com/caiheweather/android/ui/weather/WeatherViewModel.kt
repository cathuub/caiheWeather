package com.caiheweather.android.ui.weather

import androidx.lifecycle.*
import com.caiheweather.android.logic.Repository
import com.caiheweather.android.logic.model.Location
import com.caiheweather.android.logic.model.Place

class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLon = ""

    var locationLat = ""

    var placeName = ""

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)//接收页面传入的地点信息刷新内部Livedata，然后代码运行到使其触发weatherLivedata内部代码块
    }

    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lon, location.lat, placeName)//获取天气类数据
    }
    fun savePic(uri: String) = Repository.savePic(uri)
    fun getSavedPic() = Repository.getSavedPic()
    fun isPicSaved() = Repository.isPicSaved()

    fun savePlace(place: Place) = Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isPlaceSaved() = Repository.isPlaceSaved()
}