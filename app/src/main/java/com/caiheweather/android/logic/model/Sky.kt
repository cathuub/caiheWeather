package com.caiheweather.android.logic.model

import com.caiheweather.android.R

class Sky (val info: String, val icon: Int, val bg: Int)

private val sky = mapOf(
        100 to Sky("晴", R.drawable.a100, R.drawable.back_100d),
        150 to Sky("晴", R.drawable.a150, R.drawable.back_100n),
        101 to Sky("多云", R.drawable.a101, R.drawable.back_101d),
        104 to Sky("阴", R.drawable.a104, R.drawable.back_104d),
        300 to Sky("阵雨", R.drawable.a300, R.drawable.back_300d),
        301 to Sky("强阵雨", R.drawable.a301, R.drawable.back_301d),
        302 to Sky("雷阵雨", R.drawable.a302, R.drawable.back_302d),
        303 to Sky("强雷阵雨", R.drawable.a303, R.drawable.back_303d),
        305 to Sky("小雨", R.drawable.a305, R.drawable.back_305n),
        306 to Sky("中雨", R.drawable.a306, R.drawable.back_306n),
        307 to Sky("大雨", R.drawable.a307, R.drawable.back_307n),
        310 to Sky("暴雨", R.drawable.a310, R.drawable.back_310n),
        400 to Sky("小雪", R.drawable.a400, R.drawable.back_400n),
        401 to Sky("中雪", R.drawable.a401, R.drawable.back_401n),
        402 to Sky("大雪", R.drawable.a402, R.drawable.back_402n),
        403 to Sky("暴雪", R.drawable.a403, R.drawable.back_403n),
        404 to Sky("雨夹雪", R.drawable.a404,R.drawable.back_404n),
        501 to Sky("雾", R.drawable.a501, R.drawable.back_501n),
        504 to Sky("浮尘", R.drawable.a504, R.drawable.back_504n),
        511 to Sky("中度雾霾", R.drawable.a511, R.drawable.back_511n),
        512 to Sky("重度雾霾", R.drawable.a512, R.drawable.back_512n),
        999 to Sky("暂无", R.drawable.a100, R.drawable.back_100d)
        )

fun getSky(skycon:Int): Sky {
    return sky[skycon] ?: sky[999]!!
}