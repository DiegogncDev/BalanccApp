package com.onedeepath.balanccapp.di

import com.onedeepath.balanccapp.data.repository.BalanceRepositoryImpl
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class BalanceModule {

    @Binds
    abstract fun bindBalanceRepository(impl: BalanceRepositoryImpl): BalanceRepository


}