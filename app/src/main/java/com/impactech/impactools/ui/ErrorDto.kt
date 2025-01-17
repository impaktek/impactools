package com.impactech.impactools.ui


import com.impactech.impactools.impaktor.model.Error
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    @SerialName("message")
    val message: String?,
    @SerialName("error")
    val error: String?,
): Error{
    val reason get() = message ?: error ?: "Oops! Something went wrong"
}