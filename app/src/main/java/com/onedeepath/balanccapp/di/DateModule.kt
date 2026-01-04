package com.onedeepath.balanccapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DateModule {

    @Provides
    fun provideDefaultYearProvider(): () -> String = {
        java.time.LocalDate.now().year.toString()
    }
}
