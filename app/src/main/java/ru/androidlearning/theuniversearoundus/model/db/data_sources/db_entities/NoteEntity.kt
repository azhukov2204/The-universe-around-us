package ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_entity")
class NoteEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long? = null,
    @ColumnInfo(name = "note_text") var noteText: String? = null,
    @ColumnInfo(name = "creation_date") var creationDate: String? = null,
    @ColumnInfo(name = "order_number") var orderNumber: Long? = null,
    @ColumnInfo(name = "high_priority") var highPriority: Boolean? = false
)