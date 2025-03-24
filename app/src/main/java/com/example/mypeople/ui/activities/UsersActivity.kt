package com.example.mypeople.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mypeople.data.api.RetrofitClient
import com.example.mypeople.data.model.UserData
import com.example.mypeople.data.model.UserResponse
import com.example.mypeople.ui.theme.*
import retrofit2.*


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
            colors = IconButtonColors(primaryColor, contentColor, tertiaryColor, contentColor)
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
    var currentPage = 1

    // Recursive function to fetch users page by page
    fun fetchPage(page: Int) {
        RetrofitClient.instance.getUsers(page).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userData = response.body()?.data ?: emptyList()
                    updatedUsers.addAll(userData)

                    val totalPages = response.body()?.total_pages ?: 0

                    // Check if there are more pages to fetch
                    if (page < totalPages) {
                        fetchPage(page + 1)
                    } else {
                        onUsersFetched(updatedUsers)
                    }
                } else {
                    Toast.makeText(context, "Error while loading users", Toast.LENGTH_SHORT).show()
                    onUsersFetched(updatedUsers)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d("USERS", "ERROR: ${t.message ?: "ERROR UNAVAILABLE"}")
                Toast.makeText(context, "Connection error: ${t.message ?: "Unknown"}", Toast.LENGTH_SHORT).show()
                onUsersFetched(updatedUsers)
            }
        })
    }

    // Stars with the first page
    fetchPage(currentPage)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UsersScreen()
}
