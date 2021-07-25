package ru.androidlearning.theuniversearoundus.model.web

import retrofit2.Callback
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayAPI
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

interface WebRepository {
    suspend fun getPictureOfTheDayFromDataSource(dateString: String?): DataLoadState<PictureOfTheDayDTO>
}
