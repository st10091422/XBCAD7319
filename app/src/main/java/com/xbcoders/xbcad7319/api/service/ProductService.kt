package com.xbcoders.xbcad7319.api.service

import com.xbcoders.xbcad7319.api.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductService {
    // Get all products
    @GET("products")
    fun getAllProducts(@Header("Authorization") token: String): Call<List<Product>>

    // Get product by ID
    @GET("products/{id}")
    fun getProductById(@Header("Authorization") token: String,
                       @Path("id") productId: String): Call<Product>

    // Create a new product (Admin)
    @Multipart
    @POST("admin/products")
    fun createProduct(@Header("Authorization") token: String,
                      @Part("product") product: RequestBody,
                      @Part image: MultipartBody.Part): Call<Product>

    // Update a product (Admin)
    @Multipart
    @PUT("admin/products/{productId}")
    fun updateProduct(@Header("Authorization") token: String,
                      @Path("productId") productId: String,
                      @Part("product") product: RequestBody,
                      @Part image: MultipartBody.Part?): Call<Product>

    // Delete a product (Admin)
    @DELETE("admin/products/{productId}")
    fun deleteProduct(@Header("Authorization") token: String,
                      @Path("productId") productId: String): Call<Void>
}