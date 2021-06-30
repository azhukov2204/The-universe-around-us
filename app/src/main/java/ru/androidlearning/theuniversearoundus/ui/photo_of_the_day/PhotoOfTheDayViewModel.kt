package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.WebRepository
import ru.androidlearning.theuniversearoundus.model.web.WebRepositoryImpl
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

private const val ERROR_CORRUPT_DATA = "Corrupt data"

class PhotoOfTheDayViewModel(
    val photoOfTheDayLiveData: MutableLiveData<DataLoadState<PictureOfTheDayDTO>> = MutableLiveData(),
    private val webRepository: WebRepository = WebRepositoryImpl()

) : ViewModel() {
    private val callbackOfGetPictureOfTheDay: Callback<PictureOfTheDayDTO> = object : Callback<PictureOfTheDayDTO> {
        override fun onResponse(call: Call<PictureOfTheDayDTO>, response: Response<PictureOfTheDayDTO>) {
            val pictureOfTheDayDTO = response.body()
            if (response.isSuccessful && pictureOfTheDayDTO != null) {
                photoOfTheDayLiveData.postValue(DataLoadState.Success(pictureOfTheDayDTO))
            } else {
                photoOfTheDayLiveData.postValue(DataLoadState.Error(Throwable(ERROR_CORRUPT_DATA)))
            }
        }

        override fun onFailure(call: Call<PictureOfTheDayDTO>, t: Throwable) {
            photoOfTheDayLiveData.postValue(DataLoadState.Error(Throwable(t.message)))
        }
    }

    fun getPictureOfTheDayFromServer(dateString: String? = null) {
        photoOfTheDayLiveData.value = DataLoadState.Loading(null)
        webRepository.getPictureOfTheDayFromDataSource(callbackOfGetPictureOfTheDay, dateString)
    }
}
