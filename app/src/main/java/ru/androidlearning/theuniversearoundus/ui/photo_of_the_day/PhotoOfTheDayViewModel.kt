package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.WebRepository
import ru.androidlearning.theuniversearoundus.model.web.WebRepositoryImpl
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO

class PhotoOfTheDayViewModel(
    val photoOfTheDayLiveData: MutableLiveData<DataLoadState<PictureOfTheDayDTO>> = MutableLiveData(),
    private val webRepository: WebRepository = WebRepositoryImpl()

) : ViewModel() {

    fun getPictureOfTheDayFromServer(dateString: String? = null) {
        photoOfTheDayLiveData.value = DataLoadState.Loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            val dataLoadState = webRepository.getPictureOfTheDayFromDataSource(dateString)
            photoOfTheDayLiveData.postValue(dataLoadState)
        }
    }
}
