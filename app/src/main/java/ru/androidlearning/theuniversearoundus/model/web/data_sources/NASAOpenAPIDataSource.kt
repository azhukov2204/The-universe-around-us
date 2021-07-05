package ru.androidlearning.theuniversearoundus.model.web.data_sources

import retrofit2.Callback
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayAPI
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

interface NASAOpenAPIDataSource {
    suspend fun getPictureOfTheDay(dateString: String?): PictureOfTheDayDTO
}
