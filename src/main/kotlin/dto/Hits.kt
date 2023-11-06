package dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Data

@JsonIgnoreProperties(ignoreUnknown = true)
data class Hits(
    val hits: List<Hit>? = null,
    val locale: String? = null
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    data class Hit(
        @JsonProperty("osm_type") val osmType: String? = null,
        val point: Point? = null,
        val extent: List<Double>? = null,
        val country: String? = null,
        val countrycode: String? = null,
        @JsonProperty("osm_id") val osmId: String? = null,
        @JsonProperty("osm_key") val osmKey: String? = null,
        @JsonProperty("osm_value") val osmValue: String? = null,
        val city: String? = null,
        val street: String? = null,
        val postcode: String? = null,
        val housenumber: String? = null,
        @JsonProperty("house_number") val houseNumber: String? = null,
        val name: String? = null
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Point(
            val lat: Double = 0.0,
            val lng: Double = 0.0
        )
    }
}
