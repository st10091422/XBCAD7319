package com.xbcoders.xbcad7319.api.seviceimplementations

import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.api.retrofitclient
import com.xbcoders.xbcad7319.api.service.OrderService
import retrofit2.Call

class OrderServiceImpl {
    private val orderService = retrofitclient.instance.create(OrderService::class.java)

    fun placeOrder(token:String, order: Order): Call<Order> {
        return orderService.placeOrder(token, order)
    }

    fun getOrders(token:String): Call<List<Order>> {
        return orderService.getOrders(token)
    }

    fun getUserOrders(token:String, userId: String): Call<List<Order>> {
        return orderService.getUserOrders(token, userId)
    }

    fun getOrderById(token:String, orderId: String): Call<Order> {
        return orderService.getOrderById(token, orderId)
    }

    fun updateOrderStatus(token:String, orderId: String, status: Order): Call<Void> {
        return orderService.updateOrderStatus(token, orderId, status)
    }
}