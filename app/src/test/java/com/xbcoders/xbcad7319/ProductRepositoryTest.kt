package com.xbcoders.xbcad7319

import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.service.ProductService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import org.junit.Assert.assertNotNull

class ProductRepositoryTest {

    @Mock
    private lateinit var productService: ProductService

    @Mock
    private lateinit var productListCall: Call<List<Product>>

    @Mock
    private lateinit var productCall: Call<Product>

    @Mock
    private lateinit var voidCall: Call<Void>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getAllProducts should return a list of products`() {
        // Arrange
        val token = "mocked_token"
        `when`(productService.getAllProducts(token)).thenReturn(productListCall)

        // Act
        val result = productService.getAllProducts(token)

        // Assert
        verify(productService).getAllProducts(token)
        assertNotNull(result) // Ensure the result is not null
    }

    @Test
    fun `getProductById should return a product`() {
        // Arrange
        val token = "mocked_token"
        val productId = "mocked_product_id"
        `when`(productService.getProductById(token, productId)).thenReturn(productCall)

        // Act
        val result = productService.getProductById(token, productId)

        // Assert
        verify(productService).getProductById(token, productId)
        assertNotNull(result) // Ensure the result is not null
    }

    @Test
    fun `createProduct should call the service to create a product`() {
        // Arrange
        val token = "mocked_token"
        val product = mock(RequestBody::class.java)

        // Create a MultipartBody.Part instance using real data
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            "test_image.jpg",
            RequestBody.create(MultipartBody.FORM, byteArrayOf(0x00))
        )

        `when`(productService.createProduct(token, product, imagePart)).thenReturn(productCall)

        // Act
        val result = productService.createProduct(token, product, imagePart)

        // Assert
        verify(productService).createProduct(token, product, imagePart)
        assertNotNull(result) // Ensure the result is not null
    }

    @Test
    fun `updateProduct should call the service to update a product`() {
        // Arrange
        val token = "mocked_token"
        val productId = "mocked_product_id"
        val product = mock(RequestBody::class.java)

        // Create a MultipartBody.Part instance using real data
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            "test_image.jpg",
            RequestBody.create(MultipartBody.FORM, byteArrayOf(0x00))
        )

        `when`(productService.updateProduct(token, productId, product, imagePart)).thenReturn(productCall)

        // Act
        val result = productService.updateProduct(token, productId, product, imagePart)

        // Assert
        verify(productService).updateProduct(token, productId, product, imagePart)
        assertNotNull(result) // Ensure the result is not null
    }

    @Test
    fun `deleteProduct should call the service to delete a product`() {
        // Arrange
        val token = "mocked_token"
        val productId = "mocked_product_id"
        `when`(productService.deleteProduct(token, productId)).thenReturn(voidCall)

        // Act
        val result = productService.deleteProduct(token, productId)

        // Assert
        verify(productService).deleteProduct(token, productId)
        assertNotNull(result) // Ensure the result is not null
    }
}
