package ru.androidlearning.theuniversearoundus.model.web

import retrofit2.Callback
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

interface WebRepository {
    fun getPictureOfTheDayFromDataSource(callback: Callback<PictureOfTheDayDTO>, dateString: String?)
}
