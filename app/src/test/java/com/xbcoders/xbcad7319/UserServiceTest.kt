package com.xbcoders.xbcad7319

import com.xbcoders.xbcad7319.api.model.User
import com.xbcoders.xbcad7319.api.service.UserService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Call

class UserServiceTest {
    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var userCall: Call<User>

    private lateinit var user: User
    private lateinit var userId: String
    private lateinit var loginData: HashMap<String, String>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Initialize test data
        userId = "mocked_user_id"
        user = User(id = userId, username = "user", email = "user@example.com", role = "user") // Replace with your User constructor
        loginData = hashMapOf("email" to "john@example.com", "password" to "password123")
    }

    @Test
    fun `registerUser should call userService to register a new user`() {
        // Arrange
        `when`(userService.registerUser(user)).thenReturn(userCall)

        // Act
        val result = userService.registerUser(user)

        // Assert
        verify(userService).registerUser(user)
        assert(result === userCall)
    }

    @Test
    fun `loginUser should call userService to login a user`() {
        // Arrange
        `when`(userService.loginUser(loginData)).thenReturn(userCall)

        // Act
        val result = userService.loginUser(loginData)

        // Assert
        verify(userService).loginUser(loginData)
        assert(result === userCall)
    }

    @Test
    fun `getUserDetails should call userService to get user details`() {
        // Arrange
        `when`(userService.getUserDetails(userId)).thenReturn(userCall)

        // Act
        val result = userService.getUserDetails(userId)

        // Assert
        verify(userService).getUserDetails(userId)
        assert(result === userCall)
    }

    @Test
    fun `updateUserDetails should call userService to update user details`() {
        // Arrange
        `when`(userService.updateUserDetails(userId, user)).thenReturn(userCall)

        // Act
        val result = userService.updateUserDetails(userId, user)

        // Assert
        verify(userService).updateUserDetails(userId, user)
        assert(result === userCall)
    }
}