package com.example.mypeople.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mypeople.data.api.RetrofitClient
import com.example.mypeople.data.model.LoginRequest
import com.example.mypeople.data.model.LoginResponse
import com.example.mypeople.ui.userlist.UsersActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hidden by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val greyColor = androidx.compose.ui.graphics.Color.Gray
    val primaryColor = androidx.compose.ui.graphics.Color(0xFFFF4081)
    val contentColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Adress") },
            placeholder = { Text("Email Adress") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = "Email Icon") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(CornerSize(18.dp)),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Lock Icon") },
            singleLine = true,
            visualTransformation =
            if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { hidden = !hidden }) {
                    if (hidden) Icon(Icons.Outlined.Visibility, contentDescription = "Show password")
                    else Icon(Icons.Outlined.VisibilityOff, contentDescription = "Hide password")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(CornerSize(18.dp)),
        )

        // Login button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    loginUser(context, email, password)
                } else {
                    Toast.makeText(
                        context,
                        "Por favor, ingresa email y contraseña",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonColors(primaryColor, contentColor, greyColor, greyColor)
        ) {
            Text("Login")
        }
    }
}

fun loginUser(context: Context, email: String, password: String) {
    val request = LoginRequest(email, password)

    RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.isSuccessful) {
                val token = response.body()?.token
                Log.d("TOKEN", "EL TOKEN ES $token")
                Toast.makeText(context, "Login exitoso. Token: $token", Toast.LENGTH_LONG).show()
                val intent = Intent(context, UsersActivity::class.java)
                context.startActivity(intent)
            } else {
                Log.d("ERROR", "EL ERROR ES ")
                Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            // Si ocurre un fallo en la solicitud (por ejemplo, error de conexión)
            Log.d("ERROR", "EL ERROR ES $t")
            Toast.makeText(context, "Error de conexión $t. Intenta nuevamente", Toast.LENGTH_SHORT).show()
        }
    })
}