package ru.androidlearning.theuniversearoundus.model.web.data_sources.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureOfTheDayAPI {
    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(
        @Query("api_key") apiKey: String,
        @Query("date") dateString: String? = null,
    ): PictureOfTheDayDTO
}
