package ru.krackdigger.weather

data class Model(

    var current: Current?
)

data class Current(
    val dt: Long?,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Double?,
    val feels_like: Double?,
    val pressure: Int?,
    val humidity: Int?,
    val uvi: Double?,
    val clouds: Int?,
    val visibility: Long?,
    val wind_speed: Double?,
    val weather: List<Weather>?
)

data class Weather(
    val main: String?,
    val description: String?,
    val icon: String?
)