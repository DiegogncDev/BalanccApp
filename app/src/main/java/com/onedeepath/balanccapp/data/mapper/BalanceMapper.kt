package com.onedeepath.balanccapp.data.mapper

import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.domain.model.BalanceModel

fun BalanceEntity.toDomain() = BalanceModel(id = id, type = type, category = category, description = description, amount = amount, day = day, month = month, year = year)

fun BalanceModel.toEntity() = BalanceEntity(id = id, type = type, amount = amount, day = day, month = month, year = year, category = category, description = description)
