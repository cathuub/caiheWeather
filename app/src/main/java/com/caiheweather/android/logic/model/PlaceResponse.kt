package com.caiheweather.android.logic.model

class PlaceResponse(val code: String, val location: List<Place>)

class Place(val name: String,
            //val location: Location,
            val id:String,
            //@SerializedName("formatted_address") val address: String
            val lat:String,
            val lon:String,
            val adm2:String,
            val adm1:String,
            val country:String
            )

class Location(val lon: String, val lat: String)