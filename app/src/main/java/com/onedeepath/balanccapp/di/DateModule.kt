package com.onedeepath.balanccapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DateModule {

    @Provides
    fun provideDefaultYearProvider(): () -> String = {
        LocalDate.now().year.toString()
    }

    @Provides
    @DefaultMonth
    fun provideDefaultMonthProvider(): () -> String = {
        LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultMonth
