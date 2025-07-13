package com.deweiwang.bookkeeping.income

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deweiwang.bookkeeping.data.Income
import com.deweiwang.bookkeeping.expense.calculateFontSizeForText
import com.deweiwang.bookkeeping.expense.getCurrentDate
import com.deweiwang.bookkeeping.expense.getCurrentTime
import com.deweiwang.bookkeeping.expense.validateDateTime
import com.deweiwang.bookkeeping.utils.expenseTypeIcons
import kotlinx.coroutines.launch

@Composable
fun AddIncomeScreen(navController: NavController, viewModel: IncomeViewModel = hiltViewModel()) {
    var selectedType by remember { mutableStateOf<String?>(null) }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(getCurrentDate()) }
    var time by remember { mutableStateOf(getCurrentTime()) }
    var note by remember { mutableStateOf("") }

    var customType by remember { mutableStateOf("") }
    val snackbarHostState = SnackbarHostState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CustomTopBarIncome(
                title = "Record Income",
                onBackClick = { navController.popBackStack() }
            )

            Text(
                text = "Choose Expense Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            IncomeTypeGrid(
                onTypeSelected = { selectedType = it },
                selectedType = selectedType
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedType != null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextField(
                                value = if (selectedType == "Other") customType else selectedType
                                    ?: "",
                                onValueChange = {
                                    if (selectedType == "Other") {
                                        customType = it
                                    }
                                },
                                label = { Text("Type") },
                                readOnly = selectedType != "Other",
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = amount,
                                onValueChange = {
                                    if (it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                                        amount = it
                                    }
                                },
                                label = { Text("Amount") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = date,
                                onValueChange = {
                                    if (it.matches(Regex("^\\d{0,4}-\\d{0,2}-\\d{0,2}\$"))) {
                                        date = it
                                    }
                                },
                                label = { Text("Date (yyyy-MM-dd)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = time,
                                onValueChange = {
                                    if (it.matches(Regex("^\\d{0,2}:\\d{0,2}\$"))) {
                                        time = it
                                    }
                                },
                                label = { Text("Time (HH:mm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextField(
                                value = note,
                                onValueChange = { note = it },
                                label = { Text("Note") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (selectedType == "Other" && customType.isBlank()) {
                                                snackbarHostState.showSnackbar(
                                                    message = "Type cannot be empty when 'Other' is selected. Please enter a valid type.",
                                                    actionLabel = "OK"
                                                )
                                            } else if (amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
                                                snackbarHostState.showSnackbar(
                                                    message = "Amount must be greater than 0. Please enter a valid amount.",
                                                    actionLabel = "OK"
                                                )
                                            } else if (note.isEmpty()) {
                                                snackbarHostState.showSnackbar(
                                                    message = "Note cannot be empty. Please enter a note.",
                                                    actionLabel = "OK"
                                                )
                                            } else if (!validateDateTime(date, time)) {
                                                snackbarHostState.showSnackbar(
                                                    message = "Invalid date or time. Please ensure the date is not in the future, and the time is not beyond now.",
                                                    actionLabel = "OK"
                                                )
                                            } else {
                                                val income = Income(
                                                    type = if (selectedType == "Other") customType else selectedType!!,
                                                    amount = amount.toDouble(),
                                                    date = date,
                                                    time = time,
                                                    note = note
                                                )
                                                viewModel.insertIncome(income)
                                                navController.popBackStack()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Save")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncomeTypeGrid(onTypeSelected: (String) -> Unit, selectedType: String?) {
    val expenseTypes = listOf(
        "Salary", "Reward", "Borrow", "Debt", "Interest",
        "Invest", "Transaction", "Bonanza", "Other"
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val boxWidth: Dp = (screenWidth - 64.dp) / 5
    val boxHeight = boxWidth * 1.2f
    val iconSize = boxWidth * 0.6f

    Column(modifier = Modifier.padding(16.dp)) {
        val rows = expenseTypes.chunked(5)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { type ->
                    val iconRes = if (type == selectedType) {
                        expenseTypeIcons[type]?.second
                    } else {
                        expenseTypeIcons[type]?.first
                    }

                    val backgroundColor = if (type == selectedType) Color(0xFF00FFFF) else Color.LightGray

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .width(boxWidth)
                            .height(boxHeight)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onTypeSelected(type) }
                            .padding(4.dp)
                    ) {
                        iconRes?.let {
                            Icon(
                                painter = painterResource(id = it),
                                contentDescription = type,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(iconSize)
                            )
                        }

                        Text(
                            text = type,
                            color = if (type == selectedType) MaterialTheme.colorScheme.primary else Color.Gray,
                            style = if (type == selectedType) MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ) else MaterialTheme.typography.bodyMedium,
                            fontSize = calculateFontSizeForText(boxWidth),
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CustomTopBarIncome(title: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}