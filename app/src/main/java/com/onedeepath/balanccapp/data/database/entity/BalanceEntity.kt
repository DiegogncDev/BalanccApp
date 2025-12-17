package com.onedeepath.balanccapp.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.domain.model.BalanceModel

@Entity(tableName = "balance_table")
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "month") val month: String,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "category") val category: Category,
    @ColumnInfo(name = "description") val description: String
)

fun BalanceModel.toEntity() = BalanceEntity(id = id,type = type, amount = amount, day = day, month = month, year = year, category = category, description = description)