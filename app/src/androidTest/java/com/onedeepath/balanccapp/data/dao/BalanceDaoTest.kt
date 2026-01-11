package com.onedeepath.balanccapp.data.dao

import android.content.Context
import androidx.room.Room.inMemoryDatabaseBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.onedeepath.balanccapp.data.database.BalanceDatabase
import com.onedeepath.balanccapp.data.database.dao.BalanceDao
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.domain.model.Category
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BalanceDaoTest {

    private lateinit var database: BalanceDatabase
    private lateinit var balanceDao: BalanceDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = inMemoryDatabaseBuilder(context, BalanceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        balanceDao = database.getBalanceDao()

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllBalances_shouldReturnOrderedByIdDesc() = runTest {
        val b1 = BalanceEntity(id = 1, amount = 10.0, category = Category.EDUCATION, month = "January", day = "01", year = "2025", type = "income", description = "")
        val b2 = BalanceEntity(id = 2, amount = 10.0, category = Category.EDUCATION, month = "January", day = "01", year = "2025", type = "income", description = "")

        balanceDao.insertAll(b1)
        balanceDao.insertAll(b2)

        val result = balanceDao.getAllBalances().first()

        // El ID 2 debe estar primero por el DESC
        assert(result.size == 2)
        assert(result[0].id == 2)
        assert(result[1].id == 1)
    }

    @Test
    fun getBalancesByYear_shouldSumCorrectlyAndOrderMonths() = runTest {
        // GIVEN: Insertamos datos desordenados y varios del mismo mes
        val data = listOf(
            BalanceEntity(id = 1, amount = 100.0, category = Category.EDUCATION, month = "February", day = "01", year = "2024", type = "income", description = ""),
            BalanceEntity(id = 2, amount = 50.0, category = Category.EDUCATION, month = "February", day = "01", year = "2024", type = "income", description = ""), // Total Feb Income: 150
            BalanceEntity(id = 3, amount = 200.0, category = Category.EDUCATION, month = "January", day = "01", year = "2024", type = "income", description = "")  // Jan debe ir antes que Feb
        )
        data.forEach { balanceDao.insertAll(it) }

        // WHEN
        val result = balanceDao.getBalancesByYear("2024").first()

        // THEN
        assert(result.size == 2)
        // Verificamos orden del CASE (January primero)
        assert(result[0].month == "January")
        assert(result[0].total == 200.0)

        // Verificamos suma del GROUP BY
        assert(result[1].month == "February")
        assert(result[1].total == 150.0)
    }

    @Test
    fun deleteBalance_shouldRemoveCorrectItem() = runTest {
        val b1 = BalanceEntity(id = 1, amount = 100.0, category = Category.EDUCATION, month = "February", day = "01", year = "2024", type = "income", description = "")
        balanceDao.insertAll(b1)

        balanceDao.deleteBalance(1)

        val result = balanceDao.getAllBalances().first()
        assert(result.isEmpty())
    }

    @Test
    fun getBalanceByIncome_shouldOnlyReturnIncomeType() = runTest {
        val income = BalanceEntity(id = 1, amount = 100.0, category = Category.EDUCATION, month = "January", day = "01", year = "2024", type = "income", description = "")
        val expense = BalanceEntity(id = 2, amount = 50.0, category = Category.EDUCATION, month = "January", day = "01", year = "2024", type = "expense", description = "")

        balanceDao.insertAll(income)
        balanceDao.insertAll(expense)

        val result = balanceDao.getBalanceByIncome("2024", "January").first()

        assert(result.size == 1)
        assert(result[0].type == "income")
        assert(result[0].amount == 100.0)
    }

    @Test
    fun getBalanceByExpense_shouldReturnEmptyWhenNoMatch() = runTest {
        val expense = BalanceEntity(id = 1, amount = 50.0, category = Category.EDUCATION, month = "January", day = "01", year = "2024", type = "expense", description = "")
        balanceDao.insertAll(expense)

        // Buscamos un mes que no existe en la DB
        val result = balanceDao.getBalanceByExpense("2024", "December").first()

        assert(result.isEmpty())
    }

    @Test
    fun getBalancesByYear_shouldSeparateIncomeAndExpenseSameMonth() = runTest {
        val income = BalanceEntity(id = 1, amount = 100.0, category = Category.EDUCATION, month = "January", day = "01", year = "2024", type = "income", description = "")
        val expense = BalanceEntity(id = 2, amount = 40.0, category = Category.EDUCATION, month = "January", day = "01", year = "2024", type = "expense", description = "")

        balanceDao.insertAll(income)
        balanceDao.insertAll(expense)

        val result = balanceDao.getBalancesByYear("2024").first()

        // Debería haber 2 registros para enero
        assert(result.size == 2)
        assert(result.any { it.type == "income" && it.total == 100.0 })
        assert(result.any { it.type == "expense" && it.total == 40.0 })
    }

}