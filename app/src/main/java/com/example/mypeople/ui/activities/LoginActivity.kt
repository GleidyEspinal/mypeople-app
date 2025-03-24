package com.example.mypeople.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.mypeople.R
import com.example.mypeople.data.api.RetrofitClient
import com.example.mypeople.data.model.LoginRequest
import com.example.mypeople.data.model.LoginResponse
import com.example.mypeople.ui.theme.*
import retrofit2.*

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
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        //Welcome image
        Image(
            painter = painterResource(id = R.drawable.ic_login_image),
            contentDescription = "Welcome Image",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        //Welcome message
        Text(
            text = "Welcome Back",
            style = Typography.headlineSmall,
            color = baseTextColor,
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "to ",
                style = Typography.headlineMedium,
                color = baseTextColor,
            )

            Text(
                text = "my",
                style = Typography.headlineLarge,
                color = secondaryColor,
            )
            Text(
                text = "people",
                style = Typography.headlineLarge,
                color = primaryColor,
            )
        }

        //Email Address text field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label = { Text("Email Address") },
            placeholder = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = "Email Icon") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(CornerSize(18.dp)),
        )

        //Password text field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
            label = { Text("Password") },
            placeholder = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Lock Icon") },
            singleLine = true,
            visualTransformation =
            if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { hidden = !hidden }) {
                    if (hidden) Icon(Icons.Outlined.VisibilityOff, contentDescription = "Hide password")
                    else Icon(Icons.Outlined.Visibility, contentDescription = "Show password")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(CornerSize(18.dp)),
        )

        //Error message due required fields
        if (showError) {
            Text(
                text = "Email and password are required",
                color = errorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Login button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    showError = false
                    loginUser(context, email, password)
                } else {
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonColors(primaryColor, contentColor, tertiaryColor, contentColor)
        ) {
            Text("Login")
        }
    }
}

fun loginUser(context: Context, email: String, password: String) {
    val request = LoginRequest(email, password)

    //API login call
    RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
            if (response.isSuccessful) {
                val token = response.body()?.token
                Log.d("TOKEN", "THE TOKEN IS $token")

                Toast.makeText(context, "Successful login. Token: $token", Toast.LENGTH_LONG).show()

                val intent = Intent(context, UsersActivity::class.java)
                context.startActivity(intent)

            } else {
                Log.d("ERROR", "THE ERROR IS ")
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
            Log.d("ERROR", "THE ERROR IS $t")
            Toast.makeText(context, "Connection error $t. Try again", Toast.LENGTH_SHORT).show()
        }
    })
}