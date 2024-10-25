package com.xbcoders.xbcad7319.api.seviceimplementations

import com.xbcoders.xbcad7319.api.model.User
import com.xbcoders.xbcad7319.api.retrofitclient
import com.xbcoders.xbcad7319.api.service.UserService
import retrofit2.Call

class UserServiceImpl {
    private val userService = retrofitclient.instance.create(UserService::class.java)

    fun registerUser(user: User): Call<User> {
        return userService.registerUser(user)
    }

    fun loginUser(email: String, password: String): Call<User> {
        val loginData = hashMapOf("email" to email, "password" to password)
        return userService.loginUser(loginData)
    }

    fun getUserDetails(userId: String): Call<User> {
        return userService.getUserDetails(userId)
    }

    fun updateUserDetails(userId: String, user: User): Call<User> {
        return userService.updateUserDetails(userId, user)
    }
}