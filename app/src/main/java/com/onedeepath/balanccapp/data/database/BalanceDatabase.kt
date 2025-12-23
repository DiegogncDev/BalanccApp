package com.onedeepath.balanccapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.onedeepath.balanccapp.core.CategoryConverter
import com.onedeepath.balanccapp.data.database.dao.BalanceDao
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity

@Database(entities = [BalanceEntity::class], version = 3)
@TypeConverters(CategoryConverter::class)
abstract class BalanceDatabase: RoomDatabase() {

    abstract fun getBalanceDao(): BalanceDao

}