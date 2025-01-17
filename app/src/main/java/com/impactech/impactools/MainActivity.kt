package com.impactech.impactools

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.impactech.impactools.impaktor.RequestType
import com.impactech.impactools.impaktor.TimeUnit
import com.impactech.impactools.impaktor.impaktor
import com.impactech.impactools.ui.ErrorDto
import com.impactech.impactools.ui.User
import com.impactech.impactools.ui.theme.ImpactoolsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        impaktor.init(baseUrl = "mycr-auth.nugitech.com/api/v1", time = 30, unit = TimeUnit.SECONDS)
        enableEdgeToEdge()
        setContent {
            ImpactoolsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var user: User.Data? by remember { mutableStateOf(null)}
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 14.dp)
    ) {
        if(user != null){
            Text(text = "Hello, ${user!!.firstName}!")
        }

        TextField(
            value = username,label = {
                Text(text = "Email/Phone number")
            },
            placeholder = {
                Text(text = "Enter email address or phone number")
            },
            onValueChange = {
                username = it
            }
        )
        Spacer(modifier = modifier.height(4.dp))

        TextField(
            value = password,
            label = {
                Text(text = "Password")
            },
            placeholder = {
                Text(text = "Enter Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {
                password = it
            }
        )

        Spacer(modifier = modifier.height(4.dp))

        Button(
            modifier = modifier.height(54.dp).fillMaxWidth(),
            onClick = {
                scope.launch(Dispatchers.IO) {
                    loading = true
                    impaktor.networkCall<User, ErrorDto>(
                        path = "auth/login",//"auth/update-password",
                        method = RequestType.POST,
                        //authorizationToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NCwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MzYxNzU3NTV9.IH0B9V0H9y_2cgMloAZtcda-T29fUVBtfUGPrNz9Y7A",
                        body = mapOf(
                            //"old_password" to username,
                            //"new_password" to username,
                            //"confirm_password" to username
                            "identifier" to username,
                            "password" to password
                        )
                    ).apply {
                        if(isSuccessful){
                            val result = asBody
                            user = result.data
                            /*if(result.containsKey("message")){
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(context, result["message"].toString(), Toast.LENGTH_LONG).show()
                                }
                            }*/
                        }else{
                            val error = toError {
                                it.reason
                            }
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }

                        }
                        loading = false
                    }
                }
            }
        ) {
            Text(text = "Login")
            if(loading){
                Spacer(modifier.width(14.dp))
                CircularProgressIndicator(
                    color = Color.White,
                )
            }
        }
    }

    LaunchedEffect(Unit) {

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ImpactoolsTheme {
        Greeting()
    }
}