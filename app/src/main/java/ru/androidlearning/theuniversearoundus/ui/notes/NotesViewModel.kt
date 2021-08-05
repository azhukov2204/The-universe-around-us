package ru.androidlearning.theuniversearoundus.ui.notes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.androidlearning.theuniversearoundus.model.DataChangeState
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.db.DBRepository
import ru.androidlearning.theuniversearoundus.model.db.DBRepositoryImpl
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity

class NotesViewModel(
    val notesDataLoadingLiveData: MutableLiveData<DataLoadState<List<NoteEntity>>> = MutableLiveData(),
    val noteDataChangingLiveData: MutableLiveData<Pair<DataChangeState<NoteEntity>, DataManipulationTypes>> = MutableLiveData(),
    private val dbRepository: DBRepository = DBRepositoryImpl()
) : ViewModel() {

    fun getAllNotesFromDB() {
        notesDataLoadingLiveData.value = DataLoadState.Loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            val dataLoadState = dbRepository.getAllNotesFromDB()
            notesDataLoadingLiveData.postValue(dataLoadState)
        }
    }

    fun insertNoteIntoDB(noteEntity: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataChangeState = dbRepository.insertNoteIntoDB(noteEntity)
            noteDataChangingLiveData.postValue(Pair(dataChangeState, DataManipulationTypes.INSERT))

        }
    }

    fun updateNoteInDB(noteEntity: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataChangeState = dbRepository.updateNoteInDB(noteEntity)
            noteDataChangingLiveData.postValue(Pair(dataChangeState, DataManipulationTypes.UPDATE))
        }
    }

    fun deleteNoteInDB(noteEntity: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataChangeState = dbRepository.deleteNoteInDB(noteEntity)
            noteDataChangingLiveData.postValue(Pair(dataChangeState, DataManipulationTypes.DELETE))
        }
    }

    fun updateOrderNumbersInDB(notesList: List<NoteEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
                dbRepository.updateOrderNumbersInDB(notesList)
        }
    }
}
