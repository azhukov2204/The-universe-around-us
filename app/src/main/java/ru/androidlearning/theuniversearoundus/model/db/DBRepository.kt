package ru.androidlearning.theuniversearoundus.model.db

import ru.androidlearning.theuniversearoundus.model.DataChangeState
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity

interface DBRepository {
    suspend fun getAllNotesFromDB(): DataLoadState<List<NoteEntity>>
    suspend fun insertNoteIntoDB(noteEntity: NoteEntity): DataChangeState<NoteEntity>
    suspend fun deleteNoteInDB(noteEntity: NoteEntity): DataChangeState<NoteEntity>
    suspend fun updateNoteInDB(noteEntity: NoteEntity): DataChangeState<NoteEntity>
    suspend fun updateOrderNumbersInDB(notesList: List<NoteEntity>)
}