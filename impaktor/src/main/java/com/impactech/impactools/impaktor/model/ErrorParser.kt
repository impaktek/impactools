package com.impactech.impactools.impaktor.model

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <reified T, reified E: Error> Throwable.parseError(): Result<T, E> {
    printStackTrace()
    return  when (this) {
        is ResponseException -> {
            try {
                val result = response.bodyAsText()
                val error = Json.decodeFromString<E>(result)
                Result.Failure(error)
            } catch (t: Throwable) {
                Result.Error(t.message ?: "Oops! something went wrong")
            }
        }
        is SocketTimeoutException,
        is ConnectTimeoutException,
        is HttpRequestTimeoutException,
        is CancellationException -> Result.Error("Request timeout")

        is IOException -> {
            Result.Error(("A network error occurred"))
        }

        is SerializationException -> {
            println(message)
            Result.Error(("Oops! serialization error occurred"))
        }
        else -> {
            println(message)
            Result.Error(("Oops! an unexpected error occurred"))
        }
    }
}