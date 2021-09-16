package ru.krackdigger.weather

data class Movie(val name: String, val imageUrl: String, val category: String)

data class SimpleJSONModel(

    var current: Current?
)

data class Current(
    val dt: Long?,
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