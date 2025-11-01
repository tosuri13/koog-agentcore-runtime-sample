package io.github.tosuri13.kars.tools

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.agents.core.tools.reflect.tools
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResult>? = null
)

@Serializable
data class GeocodingResult(
    val latitude: Double,
    val longitude: Double,
    val name: String
)

@Serializable
data class WeatherResponse(
    val current: CurrentWeather
)

@Serializable
data class CurrentWeather(
    @SerialName("temperature_2m")
    val temperature2m: Double,

    @SerialName("apparent_temperature")
    val apparentTemperature: Double,

    @SerialName("relative_humidity_2m")
    val relativeHumidity2m: Int,

    @SerialName("wind_speed_10m")
    val windSpeed10m: Double,

    @SerialName("wind_gusts_10m")
    val windGusts10m: Double,

    @SerialName("weather_code")
    val weatherCode: Int
)

@Serializable
data class WeatherResult(
    val location: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windGust: Double,
    val conditions: String
) {
    override fun toString(): String {
        return """
            Weather forecast for $location:
            - Temperature: ${temperature}°C
            - Feels like: ${feelsLike}°C
            - Humidity: ${humidity}%
            - Wind speed: $windSpeed km/h
            - Wind gust: $windGust km/h
            - Conditions: $conditions
        """.trimIndent()
    }
}

@LLMDescription("Tools for getting weather forecasts")
class WeatherForecastTools : ToolSet {
    companion object {
        private val httpClient by lazy {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }
    }

    @Tool
    @LLMDescription("Gets the current weather forecast for a specified location.")
    suspend fun getWeather(
        @LLMDescription("The location name (e.g., 'Tokyo', 'New York', 'London')")
        location: String
    ): String {
        return try {
            val geocodingUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${encodeUrl(location)}&count=1"
            val geocodingResponse = httpClient.get(geocodingUrl).body<GeocodingResponse>()
            val geocodingResult = geocodingResponse.results?.firstOrNull()
                ?: return "Error: Location '$location' not found. Please try using English location names (e.g., 'Tokyo' instead of '東京')."
            val (latitude, longitude, name) = geocodingResult

            val weatherUrl = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$latitude" +
                    "&longitude=$longitude" +
                    "&current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,wind_gusts_10m,weather_code"
            val weatherResponse = httpClient.get(weatherUrl).body<WeatherResponse>()

            WeatherResult(
                location = name,
                temperature = weatherResponse.current.temperature2m,
                feelsLike = weatherResponse.current.apparentTemperature,
                humidity = weatherResponse.current.relativeHumidity2m,
                windSpeed = weatherResponse.current.windSpeed10m,
                windGust = weatherResponse.current.windGusts10m,
                conditions = getWeatherCondition(weatherResponse.current.weatherCode),
            ).toString()
        } catch (e: Exception) {
            "Error: Failed to get weather for '$location'. ${e.message}"
        }
    }

    private fun encodeUrl(value: String): String {
        return java.net.URLEncoder.encode(value, "UTF-8")
    }

    private fun getWeatherCondition(code: Int): String {
        val conditions = mapOf(
            0 to "Clear sky",
            1 to "Mainly clear",
            2 to "Partly cloudy",
            3 to "Overcast",
            45 to "Foggy",
            48 to "Depositing rime fog",
            51 to "Light drizzle",
            53 to "Moderate drizzle",
            55 to "Dense drizzle",
            56 to "Light freezing drizzle",
            57 to "Dense freezing drizzle",
            61 to "Slight rain",
            63 to "Moderate rain",
            65 to "Heavy rain",
            66 to "Light freezing rain",
            67 to "Heavy freezing rain",
            71 to "Slight snow fall",
            73 to "Moderate snow fall",
            75 to "Heavy snow fall",
            77 to "Snow grains",
            80 to "Slight rain showers",
            81 to "Moderate rain showers",
            82 to "Violent rain showers",
            85 to "Slight snow showers",
            86 to "Heavy snow showers",
            95 to "Thunderstorm",
            96 to "Thunderstorm with slight hail",
            99 to "Thunderstorm with heavy hail"
        )

        return conditions[code] ?: "Unknown"
    }
}

val WeatherForecastToolsRegistry = ToolRegistry {
    tools(WeatherForecastTools())
}