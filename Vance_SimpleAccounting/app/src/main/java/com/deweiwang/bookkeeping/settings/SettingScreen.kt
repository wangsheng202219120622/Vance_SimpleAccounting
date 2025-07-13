package com.deweiwang.bookkeeping.settings

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController, viewModel: SettingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val alarmEnabled by viewModel.alarmEnabled.collectAsState()
    val alarmTime by viewModel.alarmTime.collectAsState()

    var timePickerDialog by remember {
        mutableStateOf<TimePickerDialog?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable Alarm")
                Switch(
                    checked = alarmEnabled,
                    onCheckedChange = {
                        viewModel.setAlarmEnabled(it)
                        if (it) {
                            Toast.makeText(context, "Alarm Enabled", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Alarm Disabled", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            if (alarmEnabled) {
                Button(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        timePickerDialog = TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                viewModel.setAlarmTime(hourOfDay, minute)
                                timePickerDialog = null

                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePickerDialog?.show()
                    }
                ) {
                    Text(text = "Set Alarm Time: ${alarmTime ?: "Not Set"}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "About Us")
            Text(
                text = "This is a bookkeeping app developed by Dewei for CS683.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}