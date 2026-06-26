package com.tecsup.subnote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Suscripcion::class], version = 2, exportSchema = false)
abstract class SubnoteDatabase : RoomDatabase() {

    abstract fun suscripcionDao(): SuscripcionDao

    companion object {
        @Volatile
        private var INSTANCE: SubnoteDatabase? = null

        fun getDatabase(context: Context): SubnoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    SubnoteDatabase::class.java,
                    "subnote_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}