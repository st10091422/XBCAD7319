package com.xbcoders.xbcad7319.api.seviceimplementations

import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.retrofitclient
import com.xbcoders.xbcad7319.api.service.ProductService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class ProductServiceImpl {
    private val productService = retrofitclient.instance.create(ProductService::class.java)

    fun getAllProducts(token:String, ): Call<List<Product>> {
        return productService.getAllProducts(token)
    }

    fun getProductById(token:String, productId: String): Call<Product> {
        return productService.getProductById(token, productId)
    }

    fun createProduct(token:String, product: RequestBody, image: MultipartBody.Part): Call<Product> {
        return productService.createProduct(token, product, image)
    }

    fun updateProduct(token:String, productId: String, product: RequestBody, image: MultipartBody.Part?): Call<Product> {
        return productService.updateProduct(token, productId, product, image)
    }

    fun deleteProduct(token:String, productId: String): Call<Void> {
        return productService.deleteProduct(token, productId)
    }
}