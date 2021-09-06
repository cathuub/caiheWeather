package com.caiheweather.android.logic.model

class IndicesResponse(val code:String, val daily:List<dailyIndices>) {
    class dailyIndices(
        val type:String,
        val name:String,
        val level:String,
        val category:String,
        val text:String
    )
}
