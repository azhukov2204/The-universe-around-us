package ru.androidlearning.theuniversearoundus.model

sealed class DataChangeState<T> {
    data class Success<T>(val responseData: T) : DataChangeState<T>()
    data class Error<T>(val error: Throwable) : DataChangeState<T>()
}
