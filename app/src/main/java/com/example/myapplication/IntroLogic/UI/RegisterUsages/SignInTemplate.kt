import Database.RegisterUsages.IsValidCallback
import Database.RegisterUsages.encrypt
import Database.RegisterUsages.registerUser
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.myapplication.MainLogic.UI.MainWindowActivity
import com.example.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Composable
fun SignInTemplate(
    onSignIn: ((String, String) -> Unit)?,
) {
    // 🔧 State variables
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var reminder by remember {(mutableStateOf("")) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val purple = Color(0xFF8E68DE)

    val colors2 = TextFieldDefaults.colors(
        unfocusedTextColor = Color(0xFF4A148C),
        cursorColor = Color(0xFF9575CD),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = Color(0xFF9575CD)
    )


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
                        .weight(1f)
                        .scale(1.25f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                    )
            }

            Spacer(Modifier.height(20.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 18.dp)
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF9575CD),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                ) {
                    TextField(
                        value = username,
                        onValueChange = {if (it.toString().length <= 20) username = it },
                        placeholder = { Text("Username", color = Color(0xFF9575CD)) },
                        colors = colors2,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp, start = 18.dp, end = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF9575CD),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                ) {
                    TextField(
                        value = password,
                        onValueChange = {
                            if (it.toString().length <= 8) password = it },
                        visualTransformation =
                            if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                                PasswordVisualTransformation()
                            },
                        placeholder = { Text("Password", color = Color(0xFF9575CD)) },
                        colors = colors2,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                    )
                } 

                Button(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    modifier = Modifier
                        .height(50.dp)
                        .width(70.dp)
                        .scale(.85f),
                    colors = ButtonColors(
                        disabledContentColor = Color.Transparent,
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                ) {

                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Image(
                            painter = painterResource(
                                id = if (isPasswordVisible) R.drawable.ic_visibility
                                else R.drawable.ic_visibility_off
                            ),
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            }

            Row(modifier = Modifier
                .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically) {

                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .scale(.85f),
                    colors = ButtonColors(
                        disabledContentColor = Color.White,
                        containerColor = purple,
                        contentColor = Color.White,
                        disabledContainerColor = purple,
                    ),
                ) {

                    Box(modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                        ) {
                        Text(
                            text = "Register",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
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
