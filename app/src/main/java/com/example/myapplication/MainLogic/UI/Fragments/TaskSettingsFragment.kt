package com.example.myapplication.MainLogic.UI.Fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun TaskSettingsFragment_cmp(
    modifier: Modifier = Modifier,
    notificationEnabled: Boolean,
    onNotificationChange: (Boolean) -> Unit,
    repeatEnabled: Boolean,
    onRepeatChange: (Boolean) -> Unit,
    onPickTimeClick: () -> Unit) {

    val purple = Color(0xFF812BE0)
    val trackColor = Color(0xFFD2BAF7)
    val fragmentBackground = Color(0xFFF8F3FF)

    Surface(modifier = modifier
        .fillMaxWidth()
        .height(130.dp)
        .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(fragmentBackground,
                shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .clickable {onPickTimeClick() },
                verticalAlignment = Alignment.CenterVertically,
                ) {
                Text(
                    text = "Pick time",
                    color = purple,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Clock",
                    tint = purple,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 8.5.dp)
                )
            }

            //TODO: Continue...
        }
        }
    }

@Preview(showBackground = true, widthDp = 411, heightDp = 640)
@Composable
fun TaskSettingsPanelPreview() {
    var notify by remember { mutableStateOf(true) }
    var repeat by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
    ) {
        TaskSettingsFragment_cmp(
            modifier = Modifier.align(Alignment.BottomCenter),
            notificationEnabled = notify,
            onNotificationChange = { notify = it },
            repeatEnabled = repeat,
            onRepeatChange = { repeat = it },
            onPickTimeClick = { /* preview click */ }
        )
    }
}
