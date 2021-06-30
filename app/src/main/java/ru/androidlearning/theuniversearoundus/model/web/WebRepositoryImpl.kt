package ru.androidlearning.theuniversearoundus.model.web

import retrofit2.Callback
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO
import ru.androidlearning.theuniversearoundus.model.web.data_sources.NASAOpenAPIDataSource
import ru.androidlearning.theuniversearoundus.model.web.data_sources.NASAOpenAPIDataSourceImpl

class WebRepositoryImpl(
    private val nasaOpenAPIDataSource: NASAOpenAPIDataSource = NASAOpenAPIDataSourceImpl()
) : WebRepository {
    override fun getPictureOfTheDayFromDataSource(callback: Callback<PictureOfTheDayDTO>, dateString: String?) {
        nasaOpenAPIDataSource.getPictureOfTheDay(callback, dateString)
    }
}
