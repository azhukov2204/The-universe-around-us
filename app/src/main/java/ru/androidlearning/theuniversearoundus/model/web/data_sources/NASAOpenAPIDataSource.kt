package ru.androidlearning.theuniversearoundus.model.web.data_sources

import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

interface NASAOpenAPIDataSource {
    suspend fun getPictureOfTheDay(dateString: String?): PictureOfTheDayDTO
    suspend fun getPhotosOfTheUniverse(searchString: String): PhotosOfTheUniverseDTO
}
