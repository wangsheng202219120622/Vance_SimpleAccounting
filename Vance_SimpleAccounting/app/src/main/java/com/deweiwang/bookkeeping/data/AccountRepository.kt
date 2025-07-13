package com.deweiwang.bookkeeping.data

import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {
    suspend fun insertExpense(expense: Expense) {
        accountDao.insertExpense(expense)
    }

    suspend fun insertIncome(income: Income) {
        accountDao.insertIncome(income)
    }

    fun getMonthlyExpenses(month: String) = accountDao.getMonthlyExpenses(month)

    fun getMonthlyIncomes(month: String) = accountDao.getMonthlyIncomes(month)

    fun getAllExpenses() = accountDao.getAllExpenses()

    fun getAllIncomes() = accountDao.getAllIncomes()

    suspend fun getAllExpensesList(): List<Expense> {
        return accountDao.getAllExpensesList()
    }

    suspend fun getAllIncomesList(): List<Income> {
        return accountDao.getAllIncomesList()
    }

    suspend fun deleteExpense(expense: Expense) {
        accountDao.deleteExpense(expense)
    }

    suspend fun deleteIncome(income: Income) {
        accountDao.deleteIncome(income)
    }
}