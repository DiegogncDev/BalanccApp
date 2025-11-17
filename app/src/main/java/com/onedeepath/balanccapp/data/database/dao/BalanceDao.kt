package com.onedeepath.balanccapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.ui.presentation.model.BalanceByMonthEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Query("SELECT * FROM balance_table ORDER BY id DESC")
    fun getAllBalances(): Flow<List<BalanceEntity>>

    @Query("SELECT * FROM balance_table WHERE type = 'income' AND month = :month AND year = :year ORDER BY id DESC")
    fun getBalanceByIncome(year: String, month: String): Flow<List<BalanceEntity>>

    @Query("SELECT * FROM balance_table WHERE type = 'expense' AND month = :month AND year = :year ORDER BY id DESC")
    fun getBalanceByExpense(year: String, month: String): Flow<List<BalanceEntity>>

    @Query("""
        SELECT month, 
               type, 
               SUM(amount) AS total
        FROM balance_table
        WHERE year = :year
        GROUP BY month, type
        ORDER BY 
            CASE 
                WHEN month = 'January' THEN 1
                WHEN month = 'February' THEN 2
                WHEN month = 'March' THEN 3
                WHEN month = 'April' THEN 4
                WHEN month = 'May' THEN 5
                WHEN month = 'June' THEN 6
                WHEN month = 'July' THEN 7
                WHEN month = 'August' THEN 8
                WHEN month = 'September' THEN 9
                WHEN month = 'October' THEN 10
                WHEN month = 'November' THEN 11
                WHEN month = 'December' THEN 12
            END
    """)
    fun getBalancesByYear(year: String): Flow<List<BalanceByMonthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(balances: BalanceEntity)

    @Query("DELETE FROM balance_table WHERE id = :id ")
    suspend fun deleteBalance(id: Int)


}