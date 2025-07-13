package com.deweiwang.bookkeeping.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.deweiwang.bookkeeping.R
import com.deweiwang.bookkeeping.ViewState
import com.deweiwang.bookkeeping.data.Expense
import com.deweiwang.bookkeeping.data.Income
import com.deweiwang.bookkeeping.utils.expenseTypeIcons
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val showValues by viewModel.showValues.collectAsState()
    val expensesState by viewModel.expensesState.collectAsState()
    val incomesState by viewModel.incomesState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    Scaffold(
        topBar = {
            CustomTopBarHome(
                title = "Vance_SimpleAccounting",
                onSearchClick = { navController.navigate("search") },
                onSettingsClick = { navController.navigate("settings") }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_expense") },
                    modifier = Modifier.size(width = 112.dp, height = 75.dp)
                ) {
                    Text("Expense", fontSize = 18.sp)
                }

                FloatingActionButton(
                    onClick = { navController.navigate("add_income") },
                    modifier = Modifier.size(width = 112.dp, height = 75.dp)
                ) {
                    Text("Income", fontSize = 18.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            if (expensesState is ViewState.Success && incomesState is ViewState.Success) {
                val expenses = (expensesState as ViewState.Success<List<Expense>>).data
                val expenses_monthly = expenses.filter { it.date.startsWith(currentMonth) }
                val incomes = (incomesState as ViewState.Success<List<Income>>).data
                val incomes_monthly = incomes.filter { it.date.startsWith(currentMonth) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFB2DFDB)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFB2DFDB))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Monthly Expenses:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = if (showValues) "¥${expenses_monthly.sumOf { it.amount }}" else "¥********",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            IconButton(
                                onClick = { viewModel.toggleShowValues() },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    painter = painterResource(id = if (showValues) R.mipmap.visibility else R.mipmap.visible),
                                    contentDescription = if (showValues) "Hide Values" else "Show Values",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (showValues) "Monthly Incomes: ¥${incomes_monthly.sumOf { it.amount }}" else "Monthly Incomes: ¥********",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = if (showValues) "Monthly Balance: ¥${incomes_monthly.sumOf { it.amount } - expenses_monthly.sumOf { it.amount }}" else "Monthly Balance: ¥********",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = buildAnnotatedString {
                            append("Today Expense: ¥")
                            withStyle(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = Color.Black
                                )
                            ) {
                                append("${expenses.filter { it.date == todayDate }.sumOf { it.amount }}")
                            }
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Button(
                        onClick = { isEditing = !isEditing },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditing) Color.Gray else Color(0xFF3232D9)
                        ),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isEditing) "Cancel" else "Delete",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            if (!isEditing) {
                                Icon(
                                    painter = painterResource(id = R.mipmap.shanchu),
                                    contentDescription = "Delete Icon",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 110.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        val allItems = (expenses + incomes).sortedByDescending {
                            when (it) {
                                is Expense -> LocalDateTime.parse("${it.date} ${it.time}", dateTimeFormatter)
                                is Income -> LocalDateTime.parse("${it.date} ${it.time}", dateTimeFormatter)
                                else -> LocalDateTime.MIN
                            }
                        }
                        items(allItems) { item ->
                            TransactionItemCard(
                                item = item,
                                onClick = {
                                    if (isEditing) {
                                        when (item) {
                                            is Expense -> viewModel.deleteExpense(item)
                                            is Income -> viewModel.deleteIncome(item)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun TransactionItemCard(item: Any, onClick: () -> Unit = {}) {
    val backgroundColor = when (item) {
        is Expense -> Color(0xFFFFF0F1)
        is Income -> Color(0xFFE1FFFF)
        else -> Color(0xFFE1FFFF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconRes = when (item) {
                is Expense -> expenseTypeIcons[item.type]?.second ?: expenseTypeIcons["Other"]?.second
                is Income -> expenseTypeIcons[item.type]?.second ?: expenseTypeIcons["Other"]?.second
                else -> expenseTypeIcons["Other"]?.second
            } ?: R.mipmap.ic_qita_fs

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Transaction Icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (item) {
                        is Expense -> item.type
                        is Income -> item.type
                        else -> ""
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = when (item) {
                        is Expense -> item.note
                        is Income -> item.note
                        else -> ""
                    },
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "¥${when (item) {
                        is Expense -> item.amount
                        is Income -> item.amount
                        else -> 0.0
                    }}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (item) {
                        is Expense -> Color.Red
                        is Income -> Color.Green
                        else -> Color.Black
                    }
                )
                Text(
                    text = when (item) {
                        is Expense -> "${item.date} ${item.time}"
                        is Income -> "${item.date} ${item.time}"
                        else -> ""
                    },
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CustomTopBarHome(title: String, onSearchClick: () -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onSettingsClick) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
        }
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navController = rememberNavController())
}