package com.deweiwang.bookkeeping

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.deweiwang.bookkeeping.ui.theme.BookKeepingTheme
import androidx.navigation.compose.rememberNavController
import com.deweiwang.bookkeeping.data.UserPreferences
import com.deweiwang.bookkeeping.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        setContent {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val userPreferences = UserPreferences(this)
        lifecycleScope.launch {
            userPreferences.alarmEnabledFlow.collect { enabled ->
                if (enabled) {
                    userPreferences.alarmTimeFlow.collect { time ->
                        if (time != null) {
                            val parts = time.split(":")
                            val hour = parts[0].toInt()
                            val minute = parts[1].toInt()

                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)

                                if (before(Calendar.getInstance())) {
                                    add(Calendar.DAY_OF_YEAR, 1)
                                }
                            }

                            alarmManager.setInexactRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                AlarmManager.INTERVAL_DAY,
                                pendingIntent
                            )
                        }
                    }
                } else {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "bookkeeping_channel"
            val channelName = "Bookkeeping Reminders"
            val channelDescription = "Channel for bookkeeping reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BookKeepingTheme {
        Greeting("Android")
    }
}