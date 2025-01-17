
package com.impactech.impactools.impaktor

import com.impactech.impactools.impaktor.model.Error
import com.impactech.impactools.impaktor.model.Result
import com.impactech.impactools.impaktor.model.parseError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path

suspend inline fun <reified T, reified E: Error> HttpClient.get(
    path: String,
    baseUrl: String,
    //queryParams: List<QueryParams> = emptyList(),
    queryParams: MutableMap<String, Any> = mutableMapOf(),
    headers: MutableMap<String, Any> = mutableMapOf()
): Result<T, E> {
    return try {
        val result = get {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                path(path)
            }
            headers {
                contentType(ContentType.Application.Json)
                headers.forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
            if(queryParams.values.isNotEmpty()){
                queryParams.forEach { (t, u) ->
                    this.parameter(t, u.toString())
                }
            }
        }
        if(result.status.value < 300){
            Result.Success(result.body())
        }else{
            Result.Failure(result.body())
        }
    } catch (t: Throwable) {
        t.parseError()
    }
}

suspend inline fun <reified T, reified E: Error> HttpClient.post(
    baseUrl: String,
    path: String,
    body: Any? = null,
    headers: MutableMap<String, Any> = mutableMapOf()
): Result<T, E> {
    return try {
        val result = post {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                path(path)
            }
            if(body != null){
                setBody(body)
            }
            headers {
                contentType(ContentType.Application.Json)
                headers.forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
        }


        if(result.status.value < 300){
            Result.Success(result.body())
        }else{
            Result.Failure(result.body<E>())
        }

    }catch (t: Throwable){
        t.parseError()
    }
}

suspend inline fun <reified T, reified E: Error> HttpClient.put(
    baseUrl: String,
    path: String,
    body: Any? = null,
    headers: MutableMap<String, Any> = mutableMapOf()
): Result<T, E> {
    return try {
        val result = put {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                path(path)
            }
            if(body != null){
                setBody(body)
            }

            headers {
                contentType(ContentType.Application.Json)
                headers.forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
        }
        if(result.status.value < 300){
            Result.Success(result.body())
        }else{
            val error = result.body<E>()
            Result.Failure(error)
        }
    }catch (t: Throwable){
        t.parseError()
    }
}

suspend inline fun <reified T, reified E: Error> HttpClient.delete(
    baseUrl: String,
    path: String,
    body: Any? = null,
    headers: MutableMap<String, Any> = mutableMapOf()
): Result<T, E>{
    return try {
        val result = delete {
            url {
                protocol = URLProtocol.HTTPS
                host  = baseUrl
                path(path)
            }
            if(body != null){
                setBody(body)
            }
            headers {
                contentType(ContentType.Application.Json)
                headers.forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
        }
        if(result.status.value < 300){
            Result.Success(result.body())
        }else{
            val error = result.body<E>()
            Result.Failure(error)
        }
    }catch (t: Throwable){
        t.parseError()
    }
}

suspend inline fun <reified T, reified E: Error> HttpClient.patch(
    baseUrl: String,
    path: String,
    body: Any? = null,
    headers: MutableMap<String, Any> = mutableMapOf()
): Result<T, E>{
    return try {
        val result = patch {
            url {
                protocol = URLProtocol.HTTPS
                host  = baseUrl
                path(path)
            }
            if(body != null){
                setBody(body)
            }
            headers {
                contentType(ContentType.Application.Json)
                headers.forEach { (key, value) ->
                    append(key, value.toString())
                }
            }
        }
        if(result.status.value < 300){
            Result.Success(result.body())
        }else{
            val error = result.body<E>()
            Result.Failure(error)
        }
    }catch (t: Throwable){
        t.parseError()
    }
}
