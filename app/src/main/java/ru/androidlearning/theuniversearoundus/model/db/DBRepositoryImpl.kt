package ru.androidlearning.theuniversearoundus.model.db

import ru.androidlearning.theuniversearoundus.model.DataChangeState
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.db.data_sources.DBDataSource
import ru.androidlearning.theuniversearoundus.model.db.data_sources.DBDataSourceImpl
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity

class DBRepositoryImpl(
    private val dbDataSource: DBDataSource = DBDataSourceImpl()
) : DBRepository {
    override suspend fun getAllNotesFromDB(): DataLoadState<List<NoteEntity>> {
        return try {
            val noteEntitiesList = dbDataSource.getAllNotes()
            DataLoadState.Success(noteEntitiesList)
        } catch (e: Exception) {
            DataLoadState.Error(e)
        }
    }

    override suspend fun insertNoteIntoDB(noteEntity: NoteEntity): DataChangeState<NoteEntity> {
        return try {
            val noteEntityId = dbDataSource.insertNote(noteEntity)
            noteEntity.id = noteEntityId
            DataChangeState.Success(noteEntity)
        } catch (e: Exception) {
            DataChangeState.Error(e)
        }
    }

    override suspend fun deleteNoteInDB(noteEntity: NoteEntity): DataChangeState<NoteEntity> {
        return try {
            dbDataSource.deleteNote(noteEntity)
            DataChangeState.Success(noteEntity)
        } catch (e: Exception) {
            DataChangeState.Error(e)
        }
    }

    override suspend fun updateNoteInDB(noteEntity: NoteEntity): DataChangeState<NoteEntity> {
        return try {
            dbDataSource.updateNote(noteEntity)
            DataChangeState.Success(noteEntity)
        } catch (e: Exception) {
            DataChangeState.Error(e)
        }
    }

    override suspend fun updateOrderNumbersInDB(notesList: List<NoteEntity>) {
        try {
            dbDataSource.updateOrderNumbers(notesList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}