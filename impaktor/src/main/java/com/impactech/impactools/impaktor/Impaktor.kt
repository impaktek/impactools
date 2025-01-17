package com.impactech.impactools.impaktor

import com.impactech.impactools.impaktor.model.Error
import com.impactech.impactools.impaktor.model.Result
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual


typealias impaktor = Impaktor

object Impaktor {
    val http get() = HttpClient {
        install(ContentNegotiation) {
            val customSerializer = SerializersModule {
                contextual(DateTimeSerializer)
                contextual(MapWrapperSerializer)
                contextual(TimeSerializer)
                contextual(DateSerializer)
            }

            val json = Json {
                serializersModule = customSerializer
                ignoreUnknownKeys = true
                prettyPrint = true
                encodeDefaults = true
                isLenient = true
                explicitNulls = false
            }
            json(json = json, contentType = ContentType.Any)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }

            }
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = time
        }

    }

    /**
     * Performs a network call to a specified endpoint with the given parameters.
     *
     * This function is a generic wrapper for making HTTP requests, supporting GET, POST, PUT, DELETE, and PATCH methods.
     * It handles request construction, including headers, query parameters, and request bodies, and returns the result as a `Result` object.
     *
     * @param T The type of the successful response data.
     * @param E The type of the error response data, must implement the Error interface.
     * @param baseUrl The base URL of the API endpoint. Defaults to the `base` constant.
     * @param path The path to the specific endpoint (e.g., "/users/1").
     * @param method The HTTP method to use (e.g., GET, POST, PUT, DELETE, PATCH).
     * @param body The request body (for POST, PUT, PATCH, and DELETE methods). Defaults to null.
     * @param authorizationToken An optional authorization token to include in the "Authorization" header. Defaults to null.
     * @param queryParams A lambda function to dynamically construct the query parameters.
     *                    It takes a `MutableMap<String, Any>` as input (initially empty),
     *                    allows you to add or modify query parameters, and returns the modified map. Defaults to an empty map.
     * @param headers A lambda function to dynamically construct the HTTP headers.
     *                It takes a `MutableMap<String, Any>` as input (initially empty),
     *                allows you to add or modify headers, and returns the modified map. Defaults to an empty map.
     * @return A `Result<T, E>` object, which is either:
     *         - `Result.Success(T)` if the network call was successful, containing the parsed response data.
     *         - `Result.Failure(E)` if the network call failed, containing the error data.
     *
     * @throws Exception if there is an unexpected error during the network call.
     */
    suspend inline fun <reified T, reified E: Error>networkCall(
        baseUrl: String = base,
        path: String,
        method: RequestType,
        body: Any? = null,
        authorizationToken: String? = null,
        crossinline queryParams: ((MutableMap<String, Any>)->MutableMap<String, Any> ) = { mutableMapOf() },
        crossinline  headers: ((MutableMap<String, Any>)->MutableMap<String, Any> ) = { mutableMapOf() }
    ): Result<T, E> {
        val header = headers(mutableMapOf())


        authorizationToken?.let {
            header["Authorization"] = it
        }
        return when(method){
            RequestType.GET -> http.get(
                baseUrl = baseUrl,
                path = path,
                headers = header,
                queryParams = queryParams(mutableMapOf())
            )
            RequestType.POST -> http.post(
                baseUrl = baseUrl,
                path = path,
                headers = header,
                body = body
            )
            RequestType.PUT -> http.put(
                baseUrl = baseUrl,
                path = path,
                headers = header,
                body = body
            )
            RequestType.DELETE -> http.delete(
                baseUrl = baseUrl,
                path = path,
                headers = header,
                body = body
            )
            RequestType.PATCH -> http.patch(
                baseUrl = baseUrl,
                path = path,
                headers = header,
                body = body
            )
        }
    }

    var base = ""
        private set
    private var time: Long = 30_000

    private fun setBaseUrl(baseUrl: String): Impaktor{
        base = baseUrl
        return  this
    }

    /**
     * Sets the timeout for HTTP requests, including request, connect, and socket timeouts.
     *
     * This function configures the HTTP client to enforce timeouts for various stages of an HTTP request.
     * It converts the provided `time` based on the given `unit` to milliseconds and applies it to:
     * - **Request Timeout:** The maximum time allowed for an entire request to complete.
     * - **Connect Timeout:** The maximum time allowed to establish a connection to the server.
     * - **Socket Timeout:** The maximum time allowed for reading data from the socket.
     *
     * @param time The timeout duration.
     * @param unit The time unit for the timeout duration (e.g., `TimeUnit.SECONDS`, `TimeUnit.MILLISECONDS`, `TimeUnit.MINUTES`).
     * @return The `Impaktor` instance, allowing for method chaining.
     * @throws IllegalArgumentException if the time is negative.
     * @sample
     * ```kotlin
     * val impaktor = Impaktor()
     *     .setTimeOut(30, TimeUnit.SECONDS) // Sets a 30-second timeout for all request stages.
     *     .setTimeOut(500, TimeUnit.MILLISECONDS) // Sets a 500-millisecond timeout
     *     .setTimeOut(2, TimeUnit.MINUTES) // Sets a 2-minute timeout
     * ```
     */
    private fun setTimeOut( time: Long, unit: TimeUnit): Impaktor {
        this.time = when(unit) {
            TimeUnit.MILLIS -> time
            TimeUnit.SECONDS -> time * 1_000
            TimeUnit.MINUTE -> time * 60 * 1_000
        }
        http.config {
            install(HttpTimeout){
                requestTimeoutMillis = time
                connectTimeoutMillis = time
                socketTimeoutMillis = time
            }
        }
        return  this
    }

    /**
     * Initializes the API client with the specified base URL.
     *
     * This function sets the base URL for all subsequent API requests.
     * It's crucial to call this function before making any network calls to ensure
     * that requests are directed to the correct server.
     *
     * @param baseUrl The base URL of the API (e.g., "https://api.example.com/v1").
     *                Must be a valid URL.
     * @throws IllegalArgumentException if the provided baseUrl is not a valid URL.
     */
    fun init(baseUrl: String){
        setBaseUrl(baseUrl)
    }

    /**
     * Initializes the configuration for the network client.
     *
     * This function sets the base URL and the timeout duration for network requests.
     *
     * @param baseUrl The base URL for all network requests. This should be a valid URL string without the https:// (e.g., "api.example.com").
     * @param time The timeout duration value.
     * @param unit The time unit for the timeout (e.g., TimeUnit.SECONDS, TimeUnit.MILLISECONDS).
     *             Common options are:
     *             - `TimeUnit.NANOSECONDS`
     *             - `TimeUnit.MICROSECONDS`
     *             - `TimeUnit.MILLISECONDS`
     *             - `TimeUnit.SECONDS`
     *             - `TimeUnit.MINUTES`
     *             - `TimeUnit.HOURS`
     *             - `TimeUnit.DAYS`
     *
     * @throws IllegalArgumentException if the `baseUrl` is empty or null.
     * @throws IllegalArgumentException if time is negative.
     * @see setTimeOut
     * @see setBaseUrl
     */
    fun init(baseUrl: String, time: Long, unit: TimeUnit){
        setTimeOut(time, unit).setBaseUrl(baseUrl)
    }

    /**
     * Initializes the timeout duration for a task or operation.
     *
     * This function sets the timeout value and its corresponding time unit.
     * It internally uses the `setTimeOut` function to configure the timeout.
     *
     * @param time The timeout duration. This represents the numerical value of the timeout.
     * @param unit The time unit for the timeout duration (e.g., TimeUnit.SECONDS, TimeUnit.MILLISECONDS).
     *             See [TimeUnit] for possible values.
     *
     * @see setTimeOut
     * @throws IllegalArgumentException if the time value is negative.
     */
    fun init(time: Long, unit: TimeUnit){
        setTimeOut(time, unit)
    }
}



/**
 * Represents units of time.
 *
 * This enum provides constants for common time units: milliseconds, seconds, and minutes.
 * Each constant can be used to represent a duration or interval in that specific unit.
 *
 * @property MILLIS Represents time in milliseconds (1/1000th of a second).
 * @property SECONDS Represents time in seconds.
 * @property MINUTE Represents time in minutes.
 */
enum class TimeUnit{
    MILLIS, SECONDS, MINUTE
}


/**
 * @brief Enumerates the different types of HTTP requests supported.
 *
 * This enum class defines the standard HTTP methods used for interacting
 * with web resources. Each member represents a distinct action to be
 * performed on a resource.
 */
enum class RequestType {
    GET, POST, PUT, DELETE, PATCH
}
