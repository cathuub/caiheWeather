package com.caiheweather.android.ui.weather


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.caiheweather.android.MainActivity
import com.caiheweather.android.R
import com.caiheweather.android.logic.MyService
import com.caiheweather.android.logic.model.Weather
import com.caiheweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.air_quality.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.sun.*
import kotlinx.android.synthetic.main.today_detail.*
import kotlinx.android.synthetic.main.wind_index.*
import java.io.File


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }//获取viewmodel实例，通过viewprovider
    lateinit var timeChangeReceiver: TimeChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }


        setContentView(R.layout.activity_weather)

        //获取intent传入数据，如果本地没有城市缓存就使用intent传入的数据
        //如果没有intent，就统一成当地经纬度


        if (viewModel.isPlaceSaved()) {//此处是view具有存储的时候，注意到intent和存储其实是一样的，因此不判断intent的值
            val place=viewModel.getSavedPlace()
            viewModel.locationLon = place.lon
            viewModel.locationLat = place.lat
            viewModel.placeName = place.name
        }else {
            //说明是第一次使用app，默认使用北京作为地点
            viewModel.locationLon = "116.41"
            viewModel.locationLat = "39.92"
            viewModel.placeName = "北京"
        }


/*
        //下面是获取具体地理信息
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
            )
        } else {//获取地理位置
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            //位置提供器，也就是实际上来定位的对象，这里选择的是GPS定位
            val locationProvider = LocationManager.PASSIVE_PROVIDER
            //获取手机中开启的位置提供器
            //开始定位,获取当前位置对象
            val location: Location = locationManager.getLastKnownLocation(locationProvider)
            while (location == null) {
                //每1s监听一次位置信息，如果位置距离改变超过1m。就执行onLocationChanged方法
                //如果第一次打开没有显示位置信息，可以退出程序重新进入，就会显示
                locationManager.requestLocationUpdates("gps", 1000, 1.00.toFloat(), locationListener())
            }
            //获取纬度
            val latitude: Double = location.latitude
            //获取经度
            val longitude: Double = location.longitude
            //发起网络请求

            //本actity观察返回值
            viewModelplace.placeLiveData.observe(this, Observer{ result ->
                //此处观察返回的数值，，
                val places = result.getOrNull()
                if (places != null) {
                    viewModelplace.placeList.clear()
                    viewModelplace.placeList.addAll(places)
                } else {
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            viewModelplace.searchPlaces("$longitude,$latitude")
            val place =viewModelplace.placeList[0]
            viewModel.locationLon = place.lon
            viewModel.locationLat = place.lat
            viewModel.placeName=place.name
        }
*/

        //如果权限被拒绝，就需要自动定位到北京，，

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {//展示天气数据选择支
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()

        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        //首页点击事件
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            //设置点击事件，更换头像
            if (viewModel.isPicSaved()) {
                //获取dao存储中的路径数据，然后显示
                val uri=viewModel.getSavedPic()!!.toUri()
                 val bitmap = getBitmapFromUri(uri)
                headp.setImageBitmap(bitmap)
            }

/*
            if (viewModel.getSavedPic()!=null){
                val uri: Uri = viewModel.getSavedPic()!!.toUri()
                headp.setImageURI(uri)
                Toast.makeText(this, viewModel.getSavedPic(),Toast.LENGTH_SHORT).show()
            }
 */
            headp.setOnClickListener{
                //设置弹出框
                AlertDialog.Builder(this)
                        .setMessage("请选择更换方式")
                        .setTitle("")
                        .setPositiveButton("拍照") { dialog, which ->
                            // 创建File对象，用于存储拍照后的图片
                            outputImage = File(externalCacheDir, "output_image.jpg")
                            if (outputImage.exists()) {
                                outputImage.delete()
                            }
                            outputImage.createNewFile()
                            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", outputImage);//fileprovider使用
                            } else {
                                Uri.fromFile(outputImage);
                            }
                            // 启动相机程序
                            viewModel.savePic(imageUri.toString())//存储路径
                            val intent = Intent("android.media.action.IMAGE_CAPTURE")
                            //
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                            startActivityForResult(intent, takePhoto)
                        }
                        .setNeutralButton("从图库选择"){ dialog, which ->
                            // 打开文件选择器
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                            // 指定只显示照片
                            intent.type = "image/*"
                            startActivityForResult(intent, fromAlbum)
                        }
                        .create()
                        .show()
                }
        }
        //滑动面板基本设置
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                        drawerView.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        })

        //滑动面板子项点击事件，内部，需要弹出搜索框
        navView.setNavigationItemSelectedListener { it ->
            when(it.itemId){
                //启动搜索页面
                R.id.search -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                /*
                R.id.myfavoir -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }*/
            }
            true
        }

        //利用广播完成前台通知的更新事情：
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.TIME_TICK")
        timeChangeReceiver = TimeChangeReceiver()
        registerReceiver(timeChangeReceiver, intentFilter)


        //点击事件，喜欢按钮
/*
        likecity.setOnClickListener {
            //进行dao存储
            val place=Place(viewModel.placeName,"",viewModel.locationLat,viewModel.locationLon,"","","")
            val places=getSavedPlace()
            places[places.]
            PlaceDao.savePlace()
        }

 */


    }

    //获取权限所需函数
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取地点信息
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show()
                    viewModel.locationLon = "116.41"
                    viewModel.locationLat = "39.92"
                    viewModel.placeName = "北京"
                }
            }
        }
    }

//更新天气
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLon, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    //显示天气信息
    private fun showWeatherInfo(weather: Weather) {
        //第二个检测点，查看weather内数据
        placeName.text = viewModel.placeName
        val realtime = weather.realtime//内有风速
        val daily = weather.daily
        val air=weather.air
        val nowweather=realtime.icon

        //启动日升日落
        val nowdata=daily[0].fxDate
        val sunrise=daily[0].sunrise
        val sunset=daily[0].sunset
        sunrise_sunset_view2.setSunriseSunsetTime("$nowdata $sunrise", "$nowdata $sunset")
        weatherLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_MOVE) {
                    sunrise_sunset_view2.startAnim()
                }
                return false
            }
        })

        //通知系统：
        val  intent=Intent(this, MyService::class.java)
        intent.putExtra("title", getSky(nowweather.toInt()).info);
        intent.putExtra("detail", "${realtime.temp.toInt()} ℃");
        intent.putExtra("icon", getSky(nowweather.toInt()).icon.toString());
        intent.putExtra("city", viewModel.placeName);
        startService(intent)

        // 填充now.xml布局中数据
        val currentTempText = "${realtime.temp.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(nowweather.toInt()).info
        val currentPM25Text = "空气指数 ${air.aqi.toInt()} "//空气质量
        currentAQI.text = currentPM25Text
        imageView2.setImageResource(getSky(nowweather.toInt()).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.size
        for (i in 0 until days) {
            //此处一个个分组填入天气预报
            val skycon = daily[i].iconDay.toInt()
            val temperaturemax = daily[i].tempMax
            val temperaturemin = daily[i].tempMin
            //以下全是findid寻找组件部分
            val view = LayoutInflater.from(this).inflate(
                    R.layout.forecast_item,
                    forecastLayout,
                    false
            )
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            //val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text =daily[i].fxDate//这是日期
            val sky = getSky(skycon)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = daily[i].textDay//这里是文字描述
            val tempText = "${temperaturemax.toInt()} ~ ${temperaturemin.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

        // 填充life_index.xml布局中的数据（生活指数）

        val indices=weather.indices//是数组元素
        coldRiskText.text = indices[3].category
        dressingText.text = indices[2].category
        ultravioletText.text =  indices[1].category
        carWashingText.text = indices[0].category
        weatherLayout.visibility = View.VISIBLE

        //填充天气布局air_quality中的数据,air

        textView.text=air.aqi
        textView2.text=air.category
        PM2_5Text.text=air.pm2p5+" μg/m3"
        PM10Text.text=air.pm10+" μg/m3"
        O3Text.text=air.o3+" μg/m3"
        COText.text=air.co+" μg/m3"
        SO2Text.text=air.so2+" μg/m3"
        NO2Text.text=air.no2+" μg/m3"

        PM10Bar.progress=air.pm10.toInt()
        PM2_5Bar.progress=air.pm2p5.toInt()
        O3Bar.progress=air.o3.toInt()
        COBar.progress=(air.co.toFloat()*10).toInt()
        SO2Bar.progress=air.so2.toInt()
        NO2Bar.progress=air.no2.toInt()

        //填充风力风向
        windPowerText.text=realtime.windScale
        windDirectionText.text=realtime.windDir
        windSpeedText.text=realtime.windSpeed
        //设置图片
        if (realtime.windSpeed.toInt()>0)
            imageView.setImageResource(R.drawable.nowind1)
        else
            imageView.setImageResource(R.drawable.havewind1)

        //填充今日详情
        zi.text=daily[0].uvIndex
        temfeel.text=realtime.windDir
        fialw.text=realtime.precip+"mm"
        tem.text=realtime.humidity+"%"
        presu.text=realtime.pressure+"HPA"
        see.text=realtime.vis+"KM"
    }


    //下面处理头像更换事件

    //头像更换变量
    val takePhoto = 1
    val fromAlbum = 2
    lateinit var imageUri: Uri
    lateinit var outputImage: File

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {

                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    headp.setImageBitmap(rotateIfRequired(bitmap))
                }

            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的照片显示
                        viewModel.savePic(uri.toString())
                        val bitmap = getBitmapFromUri(uri)
                        headp.setImageBitmap(bitmap)
                    }
                }
            }
        }

    }

    //三个辅助查找路径函数
    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        //判断系统版本
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    //下面是内时间监听广播
    inner class TimeChangeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "Time has changed", Toast.LENGTH_SHORT).show()
            refreshWeather()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
    }
}
/*
class locationListener : LocationListener {
    override fun onLocationChanged(location: Location) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {
        Log.e("位置提供器：", "启用")
    }

    override fun onProviderDisabled(provider: String) {}
}*/
