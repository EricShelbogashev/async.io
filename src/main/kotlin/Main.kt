import com.fasterxml.jackson.databind.ObjectMapper
import dto.FeatureCollection
import dto.Hits
import dto.PlaceDescription
import dto.WeatherData
import lombok.extern.log4j.Log4j2
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import java.util.Scanner
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt

@Log4j2
object MyAsyncApp {
    private val objectMapper = ObjectMapper()
    private val scanner = Scanner(System.`in`)
    private val httpClient: AsyncHttpClient = createAsyncHttpClient()

    class Configuration(
        var connectionTimeout: Int = 5000,
        var requestTimeout: Int = 10000,
        var graphHopperApiKey,
        var openWeatherMapApiKey,
        var openTripMapApiKey
    ) : Config

    private class MyAsyncAppLoader : ConfigLoader<Configuration>("Copy Finder", options()) {
        override fun loadImpl(cmd: CommandLine): Configuration {
            return Configuration().apply {
                graphHopperApiKey = cmd.getOptionValue("gh")!!,
                openWeatherMapApiKey = cmd.getOptionValue("ow")!!,
                openTripMapApiKey = cmd.getOptionValue("ot")!!,
                connectionTimeout = cmd.getOptionValue("c")?.toInt() ?: connectionTimeout
                requestTimeout = cmd.getOptionValue("r")?.toInt() ?: requestTimeout
            }
        }

        companion object {
            @JvmStatic
            private fun options(): Options {
                val options = Options()
                options.addOption(Option.builder("c")
                    .longOpt("conTime")
                    .desc("connection timeout")
                    .hasArg()
                    .required(false)
                    .build())
                options.addOption(Option.builder("gh")
                    .longOpt("graphHopperApiKey")
                    .desc("graph hopper api key")
                    .hasArg()
                    .required(false)
                    .build())
                options.addOption(Option.builder("ot")
                    .longOpt("openTripMapApiKey")
                    .desc("connection timeout")
                    .hasArg()
                    .required(false)
                    .build())
                options.addOption(Option.builder("ow")
                    .longOpt("openWeatherMapApiKey")
                    .desc("open weather map api key")
                    .hasArg()
                    .required(false)
                    .build())
                options.addOption(Option.builder("r")
                    .longOpt("reqTime")
                    .desc("request timeout")
                    .hasArg()
                    .required(false)
                    .build())
                return options
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val configuration = MyAsyncAppLoader().load(args)

        findHits().thenAcceptAsync { hit: Hits.Hit ->
            val weatherAndPlaces = findWeatherAndPlaces(hit)
            println(weatherAndPlaces.join())
        }.join()

        httpClient.close()
    }

    private fun createAsyncHttpClient(): AsyncHttpClient {
        return DefaultAsyncHttpClient(
            DefaultAsyncHttpClientConfig.Builder()
                .setConnectTimeout(configuration.connectionTimeout)
                .setRequestTimeout(configuration.requestTimeout)
                .build()
        )
    }

    private fun findHits(): CompletableFuture<Hits.Hit> {
        print("Enter a query: ")
        val suggestion = scanner.next()
        val url = "https://graphhopper.com/api/1/geocode?q=$suggestion&locale=en&key=$graphHopperApiKey"

        return httpClient.prepareGet(url)
            .execute()
            .toCompletableFuture()
            .thenApply { response ->
                val myResponse: Hits = objectMapper.readValue(response.responseBody, Hits::class.java)
                val hits: List<Hits.Hit> = myResponse.hits ?: emptyList()

                for (i in hits.indices) {
                    println("${i + 1} Name: ${hits[i].name}")
                }
                println("Select a place")
                var idx: Int
                while (true) {
                    idx = scanner.nextInt()
                    if (idx < 1 || idx >= hits.size) {
                        println("Wrong place number")
                    } else {
                        break
                    }
                }
                hits[idx - 1]
            }
    }

    private fun findWeather(hit: Hits.Hit): CompletableFuture<WeatherData> {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${hit.point?.lat}&lon=${hit.point?.lng}&appid=$openWeatherMapApiKey"
        return httpClient.prepareGet(url)
            .execute()
            .toCompletableFuture()
            .thenApply { response ->
                objectMapper.readValue(response.responseBody, WeatherData::class.java)
            }
    }

    private fun findPlaces(hit: Hits.Hit): CompletableFuture<List<FeatureCollection.Feature>> {
        val url = "http://api.opentripmap.com/0.1/en/places/radius?radius=100&lon=${hit.point?.lng}&lat=${hit.point?.lat}&apikey=$openTripMapApiKey"
        return httpClient.prepareGet(url)
            .execute()
            .toCompletableFuture()
            .thenApply { response ->
                objectMapper.readValue(response.responseBody, FeatureCollection::class.java).features ?: emptyList()
            }
    }

    private fun findDescription(feature: FeatureCollection.Feature): CompletableFuture<String> {
        val xid = feature.properties?.xid ?: ""
        val url = "http://api.opentripmap.com/0.1/ru/places/xid/$xid?apikey=$openTripMapApiKey"
        return httpClient.prepareGet(url)
            .execute()
            .toCompletableFuture()
            .thenApply { response ->
                val placeDescription: PlaceDescription = objectMapper.readValue(response.getResponseBody(), PlaceDescription::class.java)
                if (!placeDescription.name.isNullOrEmpty()) {
                    "Name: ${placeDescription.name}\nDescription: ${placeDescription.info?.descr ?: ""}"
                } else {
                    ""
                }
            }
    }

    private fun findWeatherAndPlaces(hit: Hits.Hit): CompletableFuture<String> {
        val weather = findWeather(hit)
        val places = findPlaces(hit)

        return weather.thenCombineAsync(places.stream()
            .map { feature ->
                findDescription(feature)
            }
            .reduce(
                CompletableFuture.completedFuture("")
            ) { accum, it ->
                accum.thenCombine(
                    it
                ) { res1, res2 -> "$res1$res2" }
            }
        ) { res1, res2 -> res1 + res2 }
    }
}
