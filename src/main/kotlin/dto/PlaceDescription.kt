package dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Data

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaceDescription(
    val xid: String? = null,
    val name: String? = null,
    val osm: String? = null,
    val wikidata: String? = null,
    val rate: String? = null,
    val image: String? = null,
    val wikipedia: String? = null,
    val kinds: String? = null,
    val sources: Sources? = null,
    val bbox: Bbox? = null,
    val point: Point? = null,
    val otm: String? = null,
    val info: Info? = null
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Sources(
        val geometry: String? = null,
        val attributes: Array<String>
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Bbox(
        @JsonProperty("lat_max") val latMax: Double = 0.0,
        @JsonProperty("lat_min") val latMin: Double = 0.0,
        @JsonProperty("lon_max") val lonMax: Double = 0.0,
        @JsonProperty("lon_min") val lonMin: Double = 0.0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Point(
        val lon: Double = 0.0,
        val lat: Double = 0.0
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Info(
        val descr: String? = null,
        val image: String? = null,
        @JsonProperty("img_width") val imgWidth: Int = 0
    )
}
