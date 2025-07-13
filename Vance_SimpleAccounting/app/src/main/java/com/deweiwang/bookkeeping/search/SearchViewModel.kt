package com.deweiwang.bookkeeping.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deweiwang.bookkeeping.ViewState
import com.deweiwang.bookkeeping.data.AccountRepository
import com.deweiwang.bookkeeping.data.Expense
import com.deweiwang.bookkeeping.data.Income
import com.deweiwang.bookkeeping.network.ChatCompletionRequest
import com.deweiwang.bookkeeping.network.OpenAIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<ViewState<List<Any>>>(ViewState.Loading)
    val searchResults: StateFlow<ViewState<List<Any>>> = _searchResults.asStateFlow()

    private val _analysis = MutableStateFlow("")
    val analysis: StateFlow<String> = _analysis

    private val apiKey = "" // API Key
    private val openAIService = OpenAIService.create(apiKey)

    fun searchTransactions(query: String, year: String?, month: String?) {
        viewModelScope.launch {
            try {
                val expenses = repository.getAllExpensesList()
                val incomes = repository.getAllIncomesList()

                val filteredExpenses = expenses.filter { expense ->
                    (query.isBlank() || expense.type.contains(query, ignoreCase = true) || expense.note.contains(query, ignoreCase = true)) &&
                            (year.isNullOrEmpty() || expense.date.startsWith(year)) &&
                            (month.isNullOrEmpty() || expense.date.substring(5, 7) == month.padStart(2, '0'))
                }

                val filteredIncomes = incomes.filter { income ->
                    (query.isBlank() || income.type.contains(query, ignoreCase = true) || income.note.contains(query, ignoreCase = true)) &&
                            (year.isNullOrEmpty() || income.date.startsWith(year)) &&
                            (month.isNullOrEmpty() || income.date.substring(5, 7) == month.padStart(2, '0'))
                }

                val sortedResults = (filteredExpenses + filteredIncomes).sortedByDescending { item ->
                    when (item) {
                        is Expense -> "${item.date} ${item.time}"
                        is Income -> "${item.date} ${item.time}"
                        else -> ""
                    }
                }

                _searchResults.value = ViewState.Success(sortedResults)
            } catch (e: Exception) {
                _searchResults.value = ViewState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getAccountAnalysis(results: List<Any>) {
        if (results.isEmpty()) {
            _analysis.value = "No data available for analysis."
            return
        }

        val incomeData = results.filterIsInstance<Income>().joinToString("\n") { "Income: ${it.amount} on ${it.date}" }
        val expenseData = results.filterIsInstance<Expense>().joinToString("\n") { "Expense: ${it.amount} on ${it.date}" }

        val promptContent = """
        |The following are income and expense transactions:
        |$incomeData
        |$expenseData
        |Please provide an analysis of this data in 100-150 words, highlighting important patterns or advice.
    """.trimMargin()

        // Use messages list instead of a single prompt
        val messages = listOf(
            com.deweiwang.bookkeeping.network.Message(role = "user", content = promptContent)
        )

        viewModelScope.launch {
            try {
                val request = ChatCompletionRequest(
                    model = "gpt-3.5-turbo",
                    messages = messages,
                    max_tokens = 150,
                    temperature = 0.7
                )
                val response = openAIService.generateChatCompletion(request)
                _analysis.value = response.choices.firstOrNull()?.message?.content ?: "Unable to retrieve analysis. Please try again."
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _analysis.value = "HTTP ${e.code()}: $errorBody"
            } catch (e: Exception) {
                _analysis.value = "Error occurred: ${e.message}"
            }
        }
    }
}
