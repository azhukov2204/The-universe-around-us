package ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities

import androidx.room.*

@Dao
interface NotesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(noteEntity: NoteEntity): Long

    @Update
    fun update(noteEntity: NoteEntity)

    @Delete
    fun delete(noteEntity: NoteEntity)

    @Query("SELECT * FROM note_entity ORDER BY order_number")
    fun getAllNotes(): List<NoteEntity>

    @Query("UPDATE note_entity SET order_number = :orderNo WHERE id = :id")
    fun updateOrderById(id: Long, orderNo: Long)
}