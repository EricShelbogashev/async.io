package dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Data

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
data class WeatherData(
    val coord: Coord? = null,
    val weather: Array<Weather>,
    val base: String? = null,
    val main: Main? = null,
    val visibility: Int = 0,
    val wind: Wind? = null,
    val clouds: Clouds? = null,
    val dt: Long = 0,
    val sys: Sys? = null,
    val timezone: Int = 0,
    val id: Int = 0,
    val name: String? = null,
    val cod: Int = 0
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Coord(
        val lon: Double = 0.0,
        val lat: Double = 0.0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Weather(
        val id: Int = 0,
        val main: String? = null,
        val description: String? = null,
        val icon: String? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Main(
        val temp: Double = 0.0,
        @field:JsonProperty("feels_like") val feelsLike: Double = 0.0,
        @field:JsonProperty("temp_min") val tempMin: Double = 0.0,
        @field:JsonProperty("temp_max") val tempMax: Double = 0.0,
        val pressure: Int = 0, val humidity: Int = 0,
        @field:JsonProperty("sea_level") val seaLevel: Int = 0,
        @field:JsonProperty("grnd_level") val grndLevel: Int = 0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Wind(
        val gust: Double = 0.0,
        val speed: Double = 0.0,
        val deg: Int = 0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Clouds(val all: Int = 0)

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Sys(
        val country: String? = null,
        val sunrise: Long = 0,
        val sunset: Long = 0
    )
}
