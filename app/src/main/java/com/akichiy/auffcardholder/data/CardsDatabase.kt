package com.akichiy.auffcardholder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CardDbModel::class, ContentItemDbModel::class],
    version = 2,
    exportSchema = false
)
abstract class CardsDatabase : RoomDatabase() {

    abstract fun cardsDao(): CardsDao

    companion object {

        private var instance: CardsDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): CardsDatabase {

            instance?.let { return it }

            synchronized(LOCK) {
                instance?.let { return it }

                return Room.databaseBuilder(
                    context = context,
                    klass = CardsDatabase::class.java,
                    name = "cards.db"
                ).build().also {
                    instance = it
                }
            }
        }
    }
}