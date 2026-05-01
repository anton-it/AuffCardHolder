package com.akichiy.auffcardholder.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akichiy.auffcardholder.data.CardsDao
import com.akichiy.auffcardholder.data.CardsDatabase
import com.akichiy.auffcardholder.data.CardsRepositoryImpl
import com.akichiy.auffcardholder.domain.CardsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindCardsRepository(
        impl: CardsRepositoryImpl
    ): CardsRepository

    companion object {

//        @Provides
//        @Singleton
//        fun provideDatabase(
//            @ApplicationContext context: Context
//        ): CardsDatabase {
//            return CardsDatabase.getInstance(context)
//        }

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): CardsDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = CardsDatabase::class.java,
                name = "cards.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }


        @Provides
        @Singleton
        fun provideCardsDao(
            database: CardsDatabase
        ): CardsDao {
            return database.cardsDao()
        }
    }
}