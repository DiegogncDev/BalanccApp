package com.onedeepath.balanccapp.data.database.converter

import androidx.room.TypeConverter
import com.onedeepath.balanccapp.domain.model.Category

class CategoryConverter {

    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return Category.valueOf(value)
    }
}

