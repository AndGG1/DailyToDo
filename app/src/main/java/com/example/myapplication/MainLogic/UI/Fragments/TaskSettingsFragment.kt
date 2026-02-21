package com.example.myapplication.MainLogic.UI.Fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.MainLogic.Data.Model.Days
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean
import com.example.myapplication.MainLogic.UI.MainWindowActivity
import com.example.myapplication.MainLogic.UI.ViewModels.PanelViewModel
import com.example.myapplication.NotificationSystem.Notification
import com.example.myapplication.R
import java.util.*

var contextUse: Context? = null
var timeVal: Long = 100
var pId = "id_1"
var alarmManager: AlarmManager? = null

@Composable
fun TaskSettingsFragment_cmp(
    viewModel: PanelViewModel,
    panelId: String,
    listener: MainWindowActivity.AdapterListener,
    task: TaskItemBean,
    days: Days,
    modifier: Modifier = Modifier
) {
    val purple = Color(0xFF812BE0)
    val fragmentBackground = Color(0xFFF8F3FF)
    val context = LocalContext.current
    contextUse = context
    pId = panelId
    alarmManager = contextUse!!.getSystemService(ALARM_SERVICE) as AlarmManager


    // State for switches and time
    var notificationEnabled by remember { mutableStateOf(false) }
    var repeatEnabled by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(Calendar.getInstance()) }
    var timeLabel by remember {
        mutableStateOf(
            String.format(
                "%02d:%02d",
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE)
            )
        )
    }


    if (repeatEnabled == true) {
        task.repeat = 1
        listener.updateT(task, days.currentDay)
    } else {
        task.repeat = 0
        listener.updateT(task, days.currentDay)
    }

    if (notificationEnabled) {
        scheduleNotification()

    } else cancelNotification()


    // Show TimePicker
    fun showTimePicker() {
        val hour = selectedTime.get(Calendar.HOUR_OF_DAY)
        val minute = selectedTime.get(Calendar.MINUTE)
        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)
                timeLabel = String.format("%02d:%02d", selectedHour, selectedMinute)

                timeVal = selectedTime.timeInMillis
            },
            hour,
            minute,
            true
        ).show()
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fragmentBackground, shape = RoundedCornerShape(12.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Time Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clickable { showTimePicker() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Set time  ~  " + timeLabel,
                    color = purple,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                    ,
                    textAlign = TextAlign.Start
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Clock",
                    tint = purple,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 5.dp)
                )
            }

            // Notification Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notify me",
                    color = purple,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start
                )
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = {
                        notificationEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Notification ON" else "Notification OFF",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.scale(0.7f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = purple,
                        checkedTrackColor = fragmentBackground,
                        uncheckedThumbColor = purple,
                        uncheckedTrackColor = fragmentBackground
                    )
                )
            }

            // Repeat Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Repeat task",
                    color = purple,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start
                )
                Switch(
                    checked = repeatEnabled,
                    onCheckedChange = {
                        repeatEnabled = it
                        Toast.makeText(
                            context,
                            if (it) "Repeating ON" else "Repeating OFF",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.scale(0.7f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = purple,
                        checkedTrackColor = fragmentBackground,
                        uncheckedThumbColor = purple,
                        uncheckedTrackColor = fragmentBackground
                    )
                )
            }
        }
    }
}

fun scheduleNotification() {
    val intent = Intent(contextUse!!.applicationContext, Notification::class.java).apply {
        putExtra("titleExtra", "Task")
        putExtra("textExtra", "Time to do your task!")
        putExtra("notificationIdExtra", pId.hashCode().toString())
    }

    val pendingIntent = PendingIntent.getBroadcast(
        contextUse!!.applicationContext,
        pId.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager!!.canScheduleExactAlarms()) {
            alarmManager!!.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeVal,
                pendingIntent
            )
        } else {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            contextUse?.startActivity(intent)
        }
    } else {
        alarmManager!!.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeVal,
            pendingIntent
        )
    }

    Toast.makeText(contextUse!!.applicationContext, "Scheduled", Toast.LENGTH_LONG).show()
}

fun cancelNotification() {
    val intent = Intent(contextUse!!.applicationContext, Notification::class.java).apply {
        putExtra("titleExtra", "Task")
        putExtra("textExtra", "Time to do your task!")
        putExtra("notificationIdExtra", pId.hashCode().toString())
    }

    val pendingIntent = PendingIntent.getBroadcast(contextUse!!.applicationContext, pId.hashCode(), intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE);
    alarmManager!!.cancel(pendingIntent);
}