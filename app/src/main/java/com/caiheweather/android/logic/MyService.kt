package com.caiheweather.android.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.caiheweather.android.R

class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val title= intent?.getStringExtra("title")
        val detail= intent?.getStringExtra("detail")
        val icon= intent?.getStringExtra("icon")
        val city= intent?.getStringExtra("city")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel= NotificationChannel("myservice","前台服务", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
            val notification= NotificationCompat.Builder(this,"myservice")
                    .setContentTitle("$city 当前天气为："+title)
                    .setContentText("温度："+detail)
                    .setSmallIcon(R.drawable.a102)
                    .setLargeIcon(icon?.let { BitmapFactory.decodeResource(resources, it.toInt()) })
                .build()
        startForeground(1,notification)
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

}

