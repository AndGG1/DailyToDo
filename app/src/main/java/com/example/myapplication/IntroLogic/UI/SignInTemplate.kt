import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignInTemplate(
    onSignIn: ((String, String) -> Unit)?
) {
    // 🔧 State variables
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val purple = Color(0xFF8E68DE)


    // 🧩 Layout surface
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = purple)
            .padding(4.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {

            Spacer(Modifier.height(60.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = "Sign Up",
                    color = purple,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                    )
            }

            val colors2 = TextFieldDefaults.colors(
                unfocusedTextColor = Color(0xFF4A148C),
                cursorColor = Color(0xFF9575CD),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 12.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFA175F7),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                ) {
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Username", color = Color(0xFF9575CD)) },
                        colors = colors2,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp) // Optional inner padding
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
fun SignInTemplateTest() {
    SignInTemplate(null)
}
