package ru.androidlearning.theuniversearoundus.model.web.data_sources.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosOfTheUniverseAPI {
    @GET("search")
    suspend fun getPhotosOfTheUniverse(
        @Query("q") searchString: String,
        @Query("page") page: Int = 1
    ): PhotosOfTheUniverseDTO
}