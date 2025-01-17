package com.impactech.impactools.impaktor.model

typealias  ApiError = Error

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val result: D) : Result<D, Nothing>
    data class Failure<out E: ApiError>(val error: E) : Result<Nothing, E>
    data class Error(val error: String): Result<Nothing, Nothing>


    val isSuccessful get() = this is Success

    val asBody get() = (this as Success).result

    fun toError(onFailure: (E) -> String): String{
        return if(this is Failure){
            onFailure(error)
        }else {
            this as Error
            return error
        }
    }

    fun asFailure(onFailure: (E)-> String): String{
        return  onFailure((this as Failure).error)
    }

    fun asError(): String = (this as Error).error

}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Failure -> Result.Failure(error)
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(result))
    }
}

inline fun <T, E: Error> Result<T, E>.getOrElse(defaultValue: (Any)->T): T {
    return  when(this) {
        is Result.Failure -> defaultValue(error)
        is Result.Success -> result
        is Result.Error -> defaultValue(error)
    }
}