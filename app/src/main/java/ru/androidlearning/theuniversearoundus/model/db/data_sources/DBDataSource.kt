package ru.androidlearning.theuniversearoundus.model.db.data_sources

import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NoteEntity

interface DBDataSource {
    suspend fun insertNote(noteEntity: NoteEntity): Long
    suspend fun deleteNote(noteEntity: NoteEntity)
    suspend fun updateNote(noteEntity: NoteEntity)
    suspend fun getAllNotes(): List<NoteEntity>
    suspend fun updateOrderNumbers(notesList: List<NoteEntity>)
}