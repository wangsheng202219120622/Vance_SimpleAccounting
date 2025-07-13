package com.deweiwang.bookkeeping.expense

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.TextUnit
import com.deweiwang.bookkeeping.utils.expenseTypeIcons
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.deweiwang.bookkeeping.data.Expense
import com.deweiwang.bookkeeping.home.HomeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddExpenseScreen(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel(), viewModel: ExpenseViewModel = hiltViewModel()) {
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
            CustomTopBarExpense(
                title = "Edit Expense",
                onBackClick = { navController.popBackStack() }
            )

            Text(
                text = "Choose Expense Type",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExpenseTypeGrid(
                onTypeSelected = { selectedType = it },
                selectedType = selectedType
            )

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
                                value = if (selectedType == "Other") customType else selectedType ?: "",
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
                                                val expense = Expense(
                                                    type = if (selectedType == "Other") customType else selectedType!!,
                                                    amount = amount.toDouble(),
                                                    date = date,
                                                    time = time,
                                                    note = note
                                                )
                                                viewModel.insertExpense(expense)
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
fun ExpenseTypeGrid(onTypeSelected: (String) -> Unit, selectedType: String?) {
    val expenseTypes = listOf(
        "Catering", "Transport", "Shopping", "Dressing", "Daily",
        "Entertainment", "Snack", "Tobacco & Alcohol", "Studying", "Medical",
        "Residence", "Water & Electricity", "Communication", "Relation", "Other"
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

                    val backgroundColor = if (type == selectedType) Color(0xFFFFCDD2) else Color.LightGray

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
fun calculateFontSizeForText(textWidth: Dp): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val baseWidth = screenWidth / 6 + 4.dp

    return if (textWidth <= baseWidth) 12.sp else (12 * baseWidth.value / textWidth.value).sp
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(java.util.Date())
}

fun getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(java.util.Date())
}

fun validateDateTime(inputDate: String, inputTime: String): Boolean {
    val currentDate = getCurrentDate()
    val currentTime = getCurrentTime()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    return try {
        val inputDateParsed = dateFormat.parse(inputDate)
        val currentDateParsed = dateFormat.parse(currentDate)

        if (inputDateParsed.after(currentDateParsed)) {
            return false
        }

        if (inputDate == currentDate) {
            val inputTimeParsed = timeFormat.parse(inputTime)
            val currentTimeParsed = timeFormat.parse(currentTime)

            if (inputTimeParsed.after(currentTimeParsed)) {
                return false
            }
        }

        true
    } catch (e: Exception) {
        false
    }
}

@Composable
fun CustomTopBarExpense(title: String, onBackClick: () -> Unit) {
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