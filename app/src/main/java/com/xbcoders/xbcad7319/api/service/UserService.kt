package com.xbcoders.xbcad7319.api.service

import com.xbcoders.xbcad7319.api.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    // Register a new user
    @POST("register")
    fun registerUser(@Body user: User): Call<User>

    // Login a user
    @POST("login")
    fun loginUser(@Body loginData: HashMap<String, String>): Call<User>

    // Get user details
    @GET("users/{id}")
    fun getUserDetails(@Path("id") userId: String): Call<User>

    // Update user details
    @PUT("users/{id}")
    fun updateUserDetails(@Path("id") userId: String, @Body user: User): Call<User>
}