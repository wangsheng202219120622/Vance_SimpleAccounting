package com.deweiwang.bookkeeping.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deweiwang.bookkeeping.data.AccountRepository
import com.deweiwang.bookkeeping.data.Income
import com.deweiwang.bookkeeping.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _incomesState = MutableStateFlow<ViewState<List<Income>>>(ViewState.Loading)
    val incomesState: StateFlow<ViewState<List<Income>>> = _incomesState.asStateFlow()

    fun insertIncome(income: Income) {
        viewModelScope.launch {
            repository.insertIncome(income)
        }
    }
}