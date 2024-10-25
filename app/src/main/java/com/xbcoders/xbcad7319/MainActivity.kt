package com.xbcoders.xbcad7319

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.databinding.ActivityLoginBinding
import com.xbcoders.xbcad7319.databinding.ActivityMainBinding
import com.xbcoders.xbcad7319.databinding.ActivityRegisterBinding
import com.xbcoders.xbcad7319.ui.activity.LoginActivity
import com.xbcoders.xbcad7319.ui.fragment.admin.AdminProductsFragment
import com.xbcoders.xbcad7319.ui.fragment.admin.OrdersFragment
import com.xbcoders.xbcad7319.ui.fragment.user.CartFragment
import com.xbcoders.xbcad7319.ui.fragment.user.ProductsFragment
import com.xbcoders.xbcad7319.ui.fragment.user.UserOrdersFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var localUser: LocalUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localUser = LocalUser.getInstance(this)

        // Check if the user is logged in and set up navigation accordingly
        if (isLoggedIn()) {
            setupBottomNavigation()  // Initialize bottom navigation
        } else {
            navigateToWelcome()      // Redirect to welcome screen
        }
    }

    private fun setupBottomNavigation() {
        val userRole = localUser.getUser()?.role // Retrieve the user's role (admin, agent, user)

        Log.d("user", "user: ${userRole}")
        // Load a different menu for each role
        when (userRole) {
            "admin" -> {
                binding.bottomNavigation.menu.clear() // Clear any previous menu
                binding.bottomNavigation.inflateMenu(R.menu.admin_menu) // Load admin-specific menu
                changeCurrentFragment(OrdersFragment())
            }
            "user" -> {
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.user_menu) // Load user-specific menu
                changeCurrentFragment(ProductsFragment())
            }
            else -> {
                Log.e("MainActivity", "Invalid user role")
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // admin
                R.id.admin_orders -> {
                    changeCurrentFragment(OrdersFragment())
                }
                R.id.products -> {
                    changeCurrentFragment(AdminProductsFragment())
                }
                // user
                R.id.user_home ->{
                    changeCurrentFragment(ProductsFragment())
                }
                R.id.user_orders ->{
                    changeCurrentFragment(UserOrdersFragment())
                }
                R.id.cart->{
                    changeCurrentFragment(CartFragment())
                }
                R.id.logout ->{
                    logout()
                }
            }
            true
        }
    }


    // Navigates to the authentication screens if the user is not logged in
    private fun navigateToWelcome() {
        startActivity(Intent(this, LoginActivity::class.java)) // Start WelcomeActivity
        finish() // Finish the current activity
    }

    // Checks if the user is currently logged in
    private fun isLoggedIn(): Boolean {
        val token = localUser.getToken() // Retrieve the authentication token
        val expirationTime = localUser.getTokenExpirationTime() // Get the token expiration time
        return token != null && !localUser.isTokenExpired(expirationTime) // Check if the token is valid
    }

    // Changes the current displayed fragment and updates the toolbar title
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment) // Replace the current fragment
            commit() // Commit the transaction
        }
    }

    private fun logout() {
        localUser.clearUser() // Clear the stored token
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show() // Show logout message
        startActivity(Intent(this, MainActivity::class.java)) // Restart the MainActivity
        finish()
    }
}