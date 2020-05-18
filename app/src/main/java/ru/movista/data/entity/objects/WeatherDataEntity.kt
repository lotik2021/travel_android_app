package ru.movista.data.entity.objects

data class WeatherDataEntity(
    val android_icon_url: List<String>,
    val ios_icon_url: List<String>,
    val base: String,
    val clouds: CloudsEntity,
    val cod: Long,
    val coord: CoordinatesEntity,
    val dt: Long,
    val id: Long,
    val main: WeatherMainEntity,
    val name: String,
    val sys: WeatherSysEntity,
    val timezone: Long,
    val visibility: Long,
    val weather: List<WeatherItemEntity>,
    val wind: WeatherWindEntity
)

data class CoordinatesEntity(
    val lat: Double,
    val lon: Double
)

data class CloudsEntity(
    val all: Long
)

data class WeatherMainEntity(
    val feels_like: Double,
    val humidity: Long,
    val pressure: Long,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)

data class WeatherSysEntity(
    val country: String,
    val id: Long,
    val sunrise: Long,
    val sunset: Long,
    val type: Int
)

data class WeatherItemEntity(
    val description: String,
    val icon: String,
    val id: Long,
    val main: String
)

data class WeatherWindEntity(
    val deg: Long,
    val speed: String
)