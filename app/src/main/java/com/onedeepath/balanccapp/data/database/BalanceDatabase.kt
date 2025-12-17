package com.onedeepath.balanccapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onedeepath.balanccapp.core.CategoryConverter
import com.onedeepath.balanccapp.data.database.dao.BalanceDao
import com.onedeepath.balanccapp.data.database.dao.CategoryDao
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.data.database.entity.CategoryEntity

@Database(entities = [BalanceEntity::class, CategoryEntity::class], version = 2)
@TypeConverters(CategoryConverter::class)
abstract class BalanceDatabase: RoomDatabase() {

    abstract fun getBalanceDao(): BalanceDao

    abstract fun getCategoryDao(): CategoryDao




}