package com.deweiwang.bookkeeping.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deweiwang.bookkeeping.data.AccountRepository
import com.deweiwang.bookkeeping.data.Expense
import com.deweiwang.bookkeeping.data.Income
import com.deweiwang.bookkeeping.ViewState
import com.deweiwang.bookkeeping.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _expensesState = MutableStateFlow<ViewState<List<Expense>>>(ViewState.Loading)
    val expensesState: StateFlow<ViewState<List<Expense>>> = _expensesState.asStateFlow()

    private val _incomesState = MutableStateFlow<ViewState<List<Income>>>(ViewState.Loading)
    val incomesState: StateFlow<ViewState<List<Income>>> = _incomesState.asStateFlow()

    private val _showValues = MutableStateFlow(true)
    val showValues: StateFlow<Boolean> = _showValues

    init {
        viewModelScope.launch {
            userPreferencesRepository.showValuesFlow.collect { show ->
                _showValues.value = show
            }
        }
    }

    fun toggleShowValues() {
        viewModelScope.launch {
            val newValue = !_showValues.value
            userPreferencesRepository.setShowValues(newValue)
            _showValues.value = newValue
        }
    }

    fun loadAllData() {
        viewModelScope.launch {
            try {
                combine(
                    repository.getAllExpenses(),
                    repository.getAllIncomes()
                ) { expenses, incomes ->
                    Pair(expenses, incomes)
                }.collect { (expenses, incomes) ->
                    _expensesState.value = ViewState.Success(expenses)
                    _incomesState.value = ViewState.Success(incomes)
                }
            } catch (e: Exception) {
                _expensesState.value = ViewState.Error(e.message ?: "Unknown error")
                _incomesState.value = ViewState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<Any?>(null)
    val selectedTransaction: StateFlow<Any?> = _selectedTransaction.asStateFlow()

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    //    fun initializeTestData() {
//        viewModelScope.launch {
//            val sampleExpense = Expense(type = "Food", amount = 100.0, date = "2024-11-01", time = "12:00", note = "Lunch")
//            val sampleIncome = Income(type = "Salary", amount = 2000.0, date = "2024-11-01", time = "09:00", note = "Monthly Salary")
//            repository.insertExpense(sampleExpense)
//            repository.insertIncome(sampleIncome)
//        }
//    }
}

