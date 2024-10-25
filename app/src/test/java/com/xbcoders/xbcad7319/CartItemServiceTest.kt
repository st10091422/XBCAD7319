package com.xbcoders.xbcad7319

import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Product
import com.xbcoders.xbcad7319.api.service.CartItemService
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call

class CartItemServiceTest {

    @Mock
    private lateinit var cartItemService: CartItemService

    @Mock
    private lateinit var cartItemCall: Call<CartItem>

    @Mock
    private lateinit var cartItemListCall: Call<List<CartItem>>

    private lateinit var token: String
    private lateinit var userId: String
    private lateinit var cartItem: CartItem

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        token = "mocked_token"
        userId = "mocked_user_id"
        cartItem = CartItem(id = "mocked_id", productId = "productId", quantity = 1, userId = "userid", product = Product()) // Replace with your CartItem constructor
    }

    @Test
    fun `addItemToCart should call cartItemService to add an item`() {
        // Arrange
        `when`(cartItemService.addItemToCart(token, cartItem)).thenReturn(cartItemCall)

        // Act
        val result = cartItemService.addItemToCart(token, cartItem)

        // Assert
        verify(cartItemService).addItemToCart(token, cartItem)
        assert(result === cartItemCall)
    }

    @Test
    fun `getUserCartItems should call cartItemService to get user's cart items`() {
        // Arrange
        `when`(cartItemService.getUserCartItems(token, userId)).thenReturn(cartItemListCall)

        // Act
        val result = cartItemService.getUserCartItems(token, userId)

        // Assert
        verify(cartItemService).getUserCartItems(token, userId)
        assert(result === cartItemListCall)
    }

    @Test
    fun `updateCartItem should call cartItemService to update cart item quantity`() {
        // Arrange
        `when`(cartItemService.updateCartItem(token, cartItem)).thenReturn(cartItemListCall)

        // Act
        val result = cartItemService.updateCartItem(token, cartItem)

        // Assert
        verify(cartItemService).updateCartItem(token, cartItem)
        assert(result === cartItemListCall)
    }

    @Test
    fun `removeItemFromCart should call cartItemService to remove item from cart`() {
        // Arrange
        `when`(cartItemService.removeItemFromCart(token)).thenReturn(cartItemListCall)

        // Act
        val result = cartItemService.removeItemFromCart(token)

        // Assert
        verify(cartItemService).removeItemFromCart(token)
        assert(result === cartItemListCall)
    }
}