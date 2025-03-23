package com.example.mypeople.ui.userlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mypeople.data.api.RetrofitClient
import com.example.mypeople.data.model.UserData
import com.example.mypeople.data.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UsersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UsersScreen()
        }
    }
}

@Composable
fun UsersScreen() {
    var users by remember { mutableStateOf<List<UserData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Fetch users when the screen is first launched
    LaunchedEffect(Unit) {
        fetchUsers(context, users) { newUsers ->
            users = newUsers
            isLoading = false
        }
    }

    // UI Layout
    Column(modifier = Modifier.padding(16.dp)) {
        UserSearch(searchQuery) { searchQuery = it }
        Spacer(modifier = Modifier.height(8.dp))

        val filteredUsers = users.filter {
            it.first_name.contains(searchQuery, ignoreCase = true) ||
                    it.last_name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
        }

        UsersList(users = filteredUsers)
    }

    // Show loading spinner
    if (isLoading) {
        LoadingScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun UserSearch(searchQuery: String, onQueryChanged: (String) -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val greyColor = androidx.compose.ui.graphics.Color.Gray
    val primaryColor = androidx.compose.ui.graphics.Color(0xFFFF4081)
    val contentColor = Color.White

    Row(modifier = Modifier.fillMaxWidth()) {
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("Search user") },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(CornerSize(30.dp)),
        )

        IconButton(
            onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                onQueryChanged(searchQuery)
            },
            modifier = Modifier.align(Alignment.CenterVertically),
            colors = IconButtonColors(primaryColor, contentColor, greyColor, greyColor)
        ) {
            Icon(Icons.Outlined.Search, contentDescription = "Search")
        }
    }
}


@Composable
fun UsersList(users: List<UserData>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(users) { user ->
            UserItem(user)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun UserItem(user: UserData) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberAsyncImagePainter(user.avatar),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "${user.first_name} ${user.last_name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
fun fetchUsers(context: Context, users: List<UserData>, onUsersFetched: (List<UserData>) -> Unit) {
    var updatedUsers = users.toMutableList()

    // Realizar dos solicitudes, por ejemplo, para obtener dos páginas de resultados
    for (i in 1..2) {
        RetrofitClient.instance.getUsers(i).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    updatedUsers.addAll(response.body()?.data ?: emptyList())
                    if (i == 2) {
                        onUsersFetched(updatedUsers)
                    }
                } else {
                    Toast.makeText(context, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                    if (i == 2) {
                        onUsersFetched(updatedUsers)
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d("USERS", "ERROR: ${t.message ?: "Mensaje de error no disponible"}")
                Toast.makeText(context, "Error de conexión: ${t.message ?: "Desconocido"}", Toast.LENGTH_SHORT).show()
                if (i == 2) {
                    onUsersFetched(updatedUsers)
                }
            }
        })
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UsersScreen()
}
