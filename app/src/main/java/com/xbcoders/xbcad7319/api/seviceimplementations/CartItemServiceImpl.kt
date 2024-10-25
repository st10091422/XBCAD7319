package com.xbcoders.xbcad7319.api.seviceimplementations

import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.retrofitclient
import com.xbcoders.xbcad7319.api.service.CartItemService
import retrofit2.Call

class CartItemServiceImpl {
    private val cartItemService = retrofitclient.instance.create(CartItemService::class.java)

    fun addItemToCart(token:String, cartItem: CartItem): Call<CartItem> {
        return cartItemService.addItemToCart(token, cartItem)
    }

    fun getUserCartItems(token:String, userId: String): Call<List<CartItem>> {
        return cartItemService.getUserCartItems(token, userId)
    }

    fun updateCartItem(token:String, cartItemId: String, cartItem: CartItem): Call<List<CartItem>>{
        return cartItemService.updateCartItem(token, cartItem)
    }

    fun removeItemFromCart(token:String, cartItemId: String): Call<List<CartItem>> {
        return cartItemService.removeItemFromCart(token)
    }
}