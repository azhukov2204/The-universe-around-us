package ru.androidlearning.theuniversearoundus.app

import android.app.Application
import androidx.room.Room
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NotesDAO
import ru.androidlearning.theuniversearoundus.model.db.data_sources.db_entities.NotesDatabase

const val ERROR_TEXT_CREATING_DB = "Application is null while creating DataBase"

class App : Application() {
    companion object {
        private var appInstance: App? = null
        private var notesDatabase: NotesDatabase? = null
        private const val DB_NAME = "Notes.db"

        fun getNotesDao(): NotesDAO {
            if (notesDatabase == null) {
                synchronized(NotesDatabase::class.java) {
                    if (notesDatabase == null) {
                        if (appInstance == null) throw IllegalStateException(ERROR_TEXT_CREATING_DB)
                        notesDatabase = Room.databaseBuilder(appInstance!!.applicationContext, NotesDatabase::class.java, DB_NAME)
                            .build()
                    }
                }
            }
            return notesDatabase!!.notesDao()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }
}
