package com.xbcoders.xbcad7319.api.service

import com.xbcoders.xbcad7319.api.model.CartItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartItemService {
    // Add item to cart
    @POST("cart")
    fun addItemToCart(@Header("Authorization") token: String,
                      @Body cartItem: CartItem): Call<CartItem>

    // Get user's cart items
    @GET("cart")
    fun getUserCartItems(@Header("Authorization") token: String,
                         @Query("userId") userId: String): Call<List<CartItem>>

    // Update cart item quantity
    @PUT("cart")
    fun updateCartItem(@Header("Authorization") token: String, @Body cartItem: CartItem): Call<List<CartItem>>

    // Remove item from cart
    @DELETE("cart")
    fun removeItemFromCart(@Header("Authorization") token: String): Call<List<CartItem>>
}