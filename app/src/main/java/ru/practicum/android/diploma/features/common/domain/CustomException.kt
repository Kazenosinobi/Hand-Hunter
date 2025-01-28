package ru.practicum.android.diploma.features.common.domain

sealed class CustomException : Exception() {
    data object EmptyError : CustomException()
    data object NetworkError : CustomException()
    data object ServerError : CustomException()
    data class DatabaseError(val error: String): CustomException()
    data class RequestError(val code: Int) : CustomException()
}
