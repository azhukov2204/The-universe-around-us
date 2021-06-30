package ru.androidlearning.theuniversearoundus.model.web.data_sources

import retrofit2.Callback
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

interface NASAOpenAPIDataSource {
    fun getPictureOfTheDay(
        callback: Callback<PictureOfTheDayDTO>,
        dateString: String?
    )
}
