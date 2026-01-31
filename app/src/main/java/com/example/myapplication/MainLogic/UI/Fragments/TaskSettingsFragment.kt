package com.example.myapplication.MainLogic.UI.Fragments

import android.app.TimePickerDialog
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.MainLogic.Data.Repository.PanelRepository
import com.example.myapplication.MainLogic.UI.ViewModels.PanelViewModel
import com.example.myapplication.R
import java.util.*

@Composable
fun TaskSettingsFragment_cmp(
    viewModel: PanelViewModel,
    modifier: Modifier = Modifier
) {
    val purple = Color(0xFF812BE0)
    val trackColor = Color(0xFFD2BAF7)
    val fragmentBackground = Color(0xFFF8F3FF)
    val context = LocalContext.current

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


    if (notificationEnabled) {
        //if (viewModel.check()) viewModel.upsertPanel();
    }


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
                        checkedThumbColor = trackColor,
                        checkedTrackColor = trackColor,
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
                        checkedThumbColor = trackColor,
                        checkedTrackColor = trackColor,
                        uncheckedThumbColor = purple,
                        uncheckedTrackColor = fragmentBackground
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 411, heightDp = 640)
@Composable
fun TaskSettingsPanelPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
    ) {
        TaskSettingsFragment_cmp(
            PanelViewModel(PanelRepository(LocalContext.current)),
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
