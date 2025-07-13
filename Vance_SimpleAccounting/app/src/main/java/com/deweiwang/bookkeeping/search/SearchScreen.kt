package com.deweiwang.bookkeeping.search

import android.graphics.Typeface
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deweiwang.bookkeeping.R
import com.deweiwang.bookkeeping.ViewState
import com.deweiwang.bookkeeping.data.Expense
import com.deweiwang.bookkeeping.data.Income
import com.deweiwang.bookkeeping.home.TransactionItemCard
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import java.text.SimpleDateFormat
import java.util.Locale
import com.github.mikephil.charting.data.BarEntry
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    var query by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()
    var showVisualizationButton by remember { mutableStateOf(false) }
    var showVisualization by remember { mutableStateOf(false) }
    var showAnalysisButton by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search by Type or Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4) year = it },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = month,
                    onValueChange = { if (it.length <= 2) month = it },
                    label = { Text("Month") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    viewModel.searchTransactions(query, year.takeIf { it.isNotBlank() }, month.takeIf { it.isNotBlank() })
                    showVisualizationButton = true
                    showVisualization = false
                    showAnalysisButton = false
                    analysisResult = null
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Search")
            }

            if (showVisualizationButton) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showVisualization = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Balance Visualization")
                        Icon(
                            painter = painterResource(id = R.mipmap.keshi),
                            contentDescription = "Delete Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showVisualization) {
                    item {
                        BalanceVisualizationChart(results = (searchResults as? ViewState.Success<List<Any>>)?.data ?: emptyList())
                    }

                    item {
                        if (showVisualization) {
                            Button(
                                onClick = { viewModel.getAccountAnalysis((searchResults as? ViewState.Success<List<Any>>)?.data ?: emptyList()) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Account Analysis")
                            }
                        }
                    }

                    item {
                        val analysis by viewModel.analysis.collectAsState()
                        if (analysis.isNotEmpty()) {
                            Text(
                                text = analysis,
                                modifier = Modifier.padding(16.dp),
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                when (searchResults) {
                    is ViewState.Loading -> {
                        item {
                            Text(
                                text = "Waiting for searching",
                                color = Color.LightGray,
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    is ViewState.Success -> {
                        val results = (searchResults as ViewState.Success<List<Any>>).data
                        if (results.isEmpty()) {
                            item {
                                Text("No results found.", modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        } else {
                            items(results) { item ->
                                TransactionItemCard(item)
                            }
                        }
                    }
                    is ViewState.Error -> {
                        item {
                            Text(
                                text = (searchResults as ViewState.Error).message,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun BalanceVisualizationChart(results: List<Any>) {
    if (results.isEmpty()) {
        return
    }

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val dailyIncome = mutableMapOf<String, Double>()
    val dailyExpense = mutableMapOf<String, Double>()

    results.forEach { item ->
        val date = when (item) {
            is Income -> item.date
            is Expense -> item.date
            else -> return@forEach
        }

        val amount = when (item) {
            is Income -> item.amount
            is Expense -> item.amount
            else -> 0.0
        }

        if (item is Income) {
            dailyIncome[date] = (dailyIncome[date] ?: 0.0) + amount
        } else if (item is Expense) {
            dailyExpense[date] = (dailyExpense[date] ?: 0.0) + amount
        }
    }

    val allDates = mutableListOf<String>()
    val minDate = dailyIncome.keys.minOrNull()?.let { dateFormatter.parse(it) }
    val maxDate = dailyExpense.keys.maxOrNull()?.let { dateFormatter.parse(it) }
    if (minDate != null && maxDate != null){
        val calendar = Calendar.getInstance()
        calendar.time = minDate
        while (!calendar.time.after(maxDate)) {
            allDates.add(dateFormatter.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val maxIncome = dailyIncome.values.maxOrNull() ?: 1.0
    val maxExpense = dailyExpense.values.maxOrNull() ?: 1.0

    val incomeHeightFactor = (maxExpense / maxIncome) / 2
    val barEntries = mutableListOf<BarEntry>()
    val valueLabels = mutableListOf<String>()
    val barColors = mutableListOf<Int>()

    allDates.forEachIndexed { index, date ->
        val income = dailyIncome[date] ?: 0.0
        val expense = dailyExpense[date] ?: 0.0

        if (income > 0) {
            val adjustedIncomeHeight = income * incomeHeightFactor
            barEntries.add(BarEntry(index.toFloat(), -adjustedIncomeHeight.toFloat()))
            barColors.add(Color(0xFF49FF5A).toArgb())
            valueLabels.add(income.toString())
        }

        if (expense > 0) {
            barEntries.add(BarEntry(index.toFloat(), expense.toFloat()))
            barColors.add(Color(0xFFFF4C58).toArgb())
            valueLabels.add(expense.toString())
        }
    }

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )
                description.isEnabled = false
                axisRight.isEnabled = false

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -45f
                xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(allDates)

                axisLeft.setDrawGridLines(false)
                axisLeft.axisMinimum = -(maxIncome * incomeHeightFactor).toFloat()
                axisLeft.axisMaximum = maxExpense.toFloat()
                axisLeft.labelCount = 6
                axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value < 0) {
                            val originalValue = value / incomeHeightFactor.toFloat()
                            originalValue.toInt().toString()
                        } else {
                            value.toInt().toString()
                        }
                    }
                }

                val barDataSet = BarDataSet(barEntries, "Balance Visualization").apply {
                    colors = barColors
                    valueTextSize = 10f
                    valueTypeface = Typeface.DEFAULT_BOLD
                    axisDependency = YAxis.AxisDependency.LEFT
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val entryIndex = barEntries.indexOfFirst { it.y == value }
                            return if (entryIndex >= 0 && entryIndex < valueLabels.size) {
                                valueLabels[entryIndex]
                            } else {
                                value.toString()
                            }
                        }
                    }
                }

                data = BarData(barDataSet).apply {
                    barWidth = 0.4f
                }

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}