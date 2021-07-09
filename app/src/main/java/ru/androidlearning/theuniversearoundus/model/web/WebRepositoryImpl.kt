package ru.androidlearning.theuniversearoundus.model.web

import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO
import ru.androidlearning.theuniversearoundus.model.web.data_sources.NASAOpenAPIDataSource
import ru.androidlearning.theuniversearoundus.model.web.data_sources.NASAOpenAPIDataSourceImpl
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO
import java.lang.Exception

class WebRepositoryImpl(
    private val nasaOpenAPIDataSource: NASAOpenAPIDataSource = NASAOpenAPIDataSourceImpl()
) : WebRepository {

    override suspend fun getPictureOfTheDayFromDataSource(dateString: String?): DataLoadState<PictureOfTheDayDTO> {
        return try {
            val pictureOfTheDayDTO = nasaOpenAPIDataSource.getPictureOfTheDay(dateString)
            DataLoadState.Success(pictureOfTheDayDTO)
        } catch (e: Exception) {
            DataLoadState.Error(e)
        }
    }

    override suspend fun getPhotosOfTheUniverseFromDataSource(searchString: String): DataLoadState<PhotosOfTheUniverseDTO> {
        return try {
            val photosOfTheUniverseDTO = nasaOpenAPIDataSource.getPhotosOfTheUniverse(searchString)
            DataLoadState.Success(photosOfTheUniverseDTO)
        } catch (e: Exception) {
            DataLoadState.Error(e)
        }
    }
}
