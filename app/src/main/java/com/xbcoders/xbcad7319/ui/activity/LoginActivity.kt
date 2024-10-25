package com.xbcoders.xbcad7319.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.xbcoders.xbcad7319.MainActivity
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.local.LocalUser
import com.xbcoders.xbcad7319.api.model.User
import com.xbcoders.xbcad7319.api.seviceimplementations.UserServiceImpl
import com.xbcoders.xbcad7319.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userService: UserServiceImpl
    private lateinit var localUser: LocalUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localUser = LocalUser.getInstance(this)


        userService = UserServiceImpl()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(this, email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

    }

    private fun loginUser(context: Context, email: String, password: String) {
        userService.loginUser(email, password).enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.isSuccessful) {
                    val userRes = response.body()

                    userRes?.let{
                        val userVal = it

                        val user = User(
                            id = userVal.id,
                            username = userVal.username,
                            email = userVal.email,
                            role = userVal.role
                        )
                        val expiresIn = System.currentTimeMillis() + (24 * 60 * 60 * 1000)

                        Log.d("login", "token: ${userVal.token}\nuser: ${user}")

                        localUser.saveUser(user, expiresIn, userVal.token)
                    }
                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}