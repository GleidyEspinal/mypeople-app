package com.example.mypeople.data.api

import com.example.mypeople.data.model.LoginRequest
import com.example.mypeople.data.model.LoginResponse
import com.example.mypeople.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("users")
    fun getUsers(@Query ("page") page: Int): Call<UserResponse>
}