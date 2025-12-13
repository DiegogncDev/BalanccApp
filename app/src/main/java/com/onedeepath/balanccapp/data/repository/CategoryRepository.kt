package com.onedeepath.balanccapp.data.repository

import com.onedeepath.balanccapp.data.database.dao.CategoryDao
import com.onedeepath.balanccapp.data.database.entity.CategoryEntity
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {

    suspend fun getCategoryByName(name: String): CategoryEntity? = dao.getCategoryByName(name)

    suspend fun getAllCategories(): List<CategoryEntity> = dao.getAllCategories()

    suspend fun insertCategory(category: CategoryEntity) = dao.insert(category)

}