package com.deweiwang.bookkeeping.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deweiwang.bookkeeping.data.AccountRepository
import com.deweiwang.bookkeeping.data.Expense
import com.deweiwang.bookkeeping.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _expensesState = MutableStateFlow<ViewState<List<Expense>>>(ViewState.Loading)
    val expensesState: StateFlow<ViewState<List<Expense>>> = _expensesState.asStateFlow()

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

}