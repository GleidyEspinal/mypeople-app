package com.example.mypeople.data.model

data class UserResponse(
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPages: Int,
    val data: List<UserData>
)