package com.caiheweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caiheweather.android.R
import com.caiheweather.android.logic.model.Place
import com.caiheweather.android.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            //设置点击转移到天气界面事件监听器
            var intent: Intent
            //传出地点信息，构建intent传输出数据,注意要查看是否有本地存储
            /*
            if (fragment.viewModel.isPlaceSaved()) {//实现判断存储功能，如果有存就直接进入天气界面，不再进入搜索页面
                val place = fragment.viewModel.getSavedPlace()
                intent = Intent(context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.lon)
                    putExtra("location_lat", place.lat)
                    putExtra("place_name", place.name)
                    fragment.viewModel.savePlace(place)
                }
            } else {

             */
                val position = holder.adapterPosition
                val place = placeList[position]
                intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.lon)
                    putExtra("location_lat", place.lat)
                    putExtra("place_name", place.name)
                }
            fragment.viewModel.savePlace(place)
            //存储信息
            fragment.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.adm2+" "+place.adm1+" "+place.country
    }

    override fun getItemCount() = placeList.size

}
