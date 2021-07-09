package ru.androidlearning.theuniversearoundus.model.web.data_sources

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.androidlearning.theuniversearoundus.BuildConfig
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseAPI
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayAPI
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO
import java.io.IOException

private const val BASE_NASA_URL = "https://api.nasa.gov/"
private const val BASE_NASA_IMAGES_BASE_URL = "https://images-api.nasa.gov/"

class NASAOpenAPIDataSourceImpl : NASAOpenAPIDataSource {
    override suspend fun getPictureOfTheDay(dateString: String?): PictureOfTheDayDTO =
        Retrofit.Builder()
            .baseUrl(BASE_NASA_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(PictureOfTheDayInterceptor()))
            .build()
            .create(PictureOfTheDayAPI::class.java)
            .getPictureOfTheDay(BuildConfig.NASA_API_KEY, dateString)

    override suspend fun getPhotosOfTheUniverse(searchString: String): PhotosOfTheUniverseDTO =
        Retrofit.Builder()
            .baseUrl(BASE_NASA_IMAGES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(PictureOfTheDayInterceptor()))
            .build()
            .create(PhotosOfTheUniverseAPI::class.java)
            .getPhotosOfTheUniverse(searchString)

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    inner class PictureOfTheDayInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain.proceed(chain.request())
        }
    }
}
