package com.onedeepath.balanccapp.di

import android.content.Context
import androidx.room.Room
import com.onedeepath.balanccapp.data.database.BalanceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val BALANCE_DATABASE_NAME = "balance_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(context, BalanceDatabase::class.java, BALANCE_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideBalanceDao(db: BalanceDatabase) = db.getBalanceDao()

    @Singleton
    @Provides
    fun provideCategoryDao(db: BalanceDatabase) = db.getCategoryDao()




}