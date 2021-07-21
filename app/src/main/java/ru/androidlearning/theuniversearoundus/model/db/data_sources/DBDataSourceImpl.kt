package ru.androidlearning.theuniversearoundus.model.db.data_sources

import ru.androidlearning.theuniversearoundus.app.App
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NotesDAO

class DBDataSourceImpl : DBDataSource {
    override suspend fun insertNote(noteEntity: NoteEntity): Long {
        return notesDAO.insert(noteEntity)
    }

    override suspend fun deleteNote(noteEntity: NoteEntity) {
        notesDAO.delete(noteEntity)
    }

    override suspend fun updateNote(noteEntity: NoteEntity) {
        notesDAO.update(noteEntity)
    }

    override suspend fun updateOrderNumbers(notesList: List<NoteEntity>) {
        for (noteEntity in notesList) {
            noteEntity.id?.let { notesDAO.updateOrderById(it, notesList.indexOf(noteEntity).toLong()) }
        }
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        return notesDAO.getAllNotes()
    }

    companion object {
        private val notesDAO: NotesDAO = App.getNotesDao()
    }
}