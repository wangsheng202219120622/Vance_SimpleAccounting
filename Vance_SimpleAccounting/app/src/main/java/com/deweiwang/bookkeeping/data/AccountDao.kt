package com.deweiwang.bookkeeping.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Insert
    suspend fun insertIncome(income: Income)

    @Query("SELECT * FROM expense_table WHERE date LIKE :month || '%'")
    fun getMonthlyExpenses(month: String): Flow<List<Expense>>

    @Query("SELECT * FROM income_table WHERE date LIKE :month || '%'")
    fun getMonthlyIncomes(month: String): Flow<List<Income>>

    @Query("SELECT * FROM expense_table")
    suspend fun getAllExpensesList(): List<Expense>

    @Query("SELECT * FROM income_table")
    suspend fun getAllIncomesList(): List<Income>

    @Query("SELECT * FROM expense_table")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM income_table")
    fun getAllIncomes(): Flow<List<Income>>

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Delete
    suspend fun deleteIncome(income: Income)
}