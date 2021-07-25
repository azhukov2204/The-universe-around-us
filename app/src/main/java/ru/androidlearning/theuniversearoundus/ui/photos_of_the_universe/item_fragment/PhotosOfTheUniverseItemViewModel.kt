package ru.androidlearning.theuniversearoundus.ui.photos_of_the_universe.item_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.WebRepository
import ru.androidlearning.theuniversearoundus.model.web.WebRepositoryImpl
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO

class PhotosOfTheUniverseItemViewModel(
    val photosOfTheUniverseLiveData: MutableLiveData<DataLoadState<PhotosOfTheUniverseDTO>> = MutableLiveData(),
    private val webRepository: WebRepository = WebRepositoryImpl()
) : ViewModel() {

    fun getPhotosOfTheUniverseFromServer(searchString: String) {
        photosOfTheUniverseLiveData.value = DataLoadState.Loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            val dataLoadState = webRepository.getPhotosOfTheUniverseFromDataSource(searchString)
            photosOfTheUniverseLiveData.postValue(dataLoadState)
        }
    }
}
