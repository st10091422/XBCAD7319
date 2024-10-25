package com.xbcoders.xbcad7319.api.service

import com.xbcoders.xbcad7319.api.model.Order
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderService {
    // Place a new order
    @POST("orders")
    fun placeOrder(@Header("Authorization") token: String, @Body order: Order): Call<Order>

    // Get all orders for a user
    @GET("get-all-orders")
    fun getOrders(@Header("Authorization") token: String): Call<List<Order>>

    // Get all orders for a user
    @GET("orders")
    fun getUserOrders(@Header("Authorization") token: String,@Query("userId") userId: String): Call<List<Order>>

    // Get specific order by ID
    @GET("orders/{id}")
    fun getOrderById(@Header("Authorization") token: String,@Path("id") orderId: String): Call<Order>

    // Update order status (Admin only)
    @PUT("admin/orders/{orderId}")
    fun updateOrderStatus(@Header("Authorization") token: String,@Path("orderId") orderId: String, @Body status: Order): Call<Void>
}