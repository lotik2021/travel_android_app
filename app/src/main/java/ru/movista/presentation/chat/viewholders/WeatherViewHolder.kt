package ru.movista.presentation.chat.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_weather.view.*
import ru.movista.R
import ru.movista.presentation.chat.WeatherItem
import ru.movista.presentation.utils.inject
import ru.movista.utils.getString

class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(item: WeatherItem) {
        with(itemView) {
            weather_description.text = item.weatherViewModel.description.capitalize()
            temperature.text = item.weatherViewModel.temperature.toString()
            wind.text = getString(R.string.speed_pattern, item.weatherViewModel.windSpeed)
            pressure.text = getString(R.string.pressure_pattern, item.weatherViewModel.pressure)
            humidity.text = getString(R.string.percent_pattern, item.weatherViewModel.humidity)
            weather_icon.inject(item.weatherViewModel.iconUrl, item.weatherViewModel.iosIconUrl)
        }
    }
}