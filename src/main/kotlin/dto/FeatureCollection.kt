package dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FeatureCollection(
    val type: String? = null,
    val features: List<Feature>? = null
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Feature(
        val type: String? = null,
        val id: String? = null,
        val geometry: Geometry? = null,
        val properties: Properties? = null
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Geometry(
            val coordinates: DoubleArray,
            val type: String? = null
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Properties(
            val xid: String? = null,
            val name: String? = null,
            val dist: Double = 0.0,
            val rate: Int = 0,
            val osm: String? = null,
            val kinds: String? = null,
            val wikidata: String? = null
        )
    }
}
