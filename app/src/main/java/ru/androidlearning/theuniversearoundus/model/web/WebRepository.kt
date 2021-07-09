package ru.androidlearning.theuniversearoundus.model.web

import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

interface WebRepository {
    suspend fun getPictureOfTheDayFromDataSource(dateString: String?): DataLoadState<PictureOfTheDayDTO>
    suspend fun getPhotosOfTheUniverseFromDataSource(searchString: String): DataLoadState<PhotosOfTheUniverseDTO>
}
