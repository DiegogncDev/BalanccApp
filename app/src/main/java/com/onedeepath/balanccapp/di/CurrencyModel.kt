package com.onedeepath.balanccapp.di

import com.onedeepath.balanccapp.core.AndroidCurrencyHelper
import com.onedeepath.balanccapp.core.CurrencyHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CurrencyModel {

    @Binds
    abstract fun bindCurrencyHelper(
        impl: AndroidCurrencyHelper
    ): CurrencyHelper
}