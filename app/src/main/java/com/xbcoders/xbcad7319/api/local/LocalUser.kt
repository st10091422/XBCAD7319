package com.xbcoders.xbcad7319.api.local

import android.content.Context
import android.content.SharedPreferences
import com.xbcoders.xbcad7319.api.model.User

class LocalUser private constructor(context: Context) {
    // SharedPreferences instance to store user data
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Save user data to shared preferences

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun saveUser(user: User, expiresIn: Long, token: String) {
        // Create an editor to modify shared preferences
        val editor = sharedPreferences.edit()

        // Store user ID, username, and email
        editor.putString("userid", user.id)
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.putLong("is_logged_in", expiresIn)
        editor.putString("token", token)
        editor.putString("role", user.role)

        // Apply changes asynchronously
        editor.apply()
    }

    // Retrieve user data from shared preferences

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun getUser(): User? {
        // Get user details from shared preferences
        val userid = sharedPreferences.getString("userid", null)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        val role = sharedPreferences.getString("role", null)

        // Create a User object; if any value is missing, return an empty User
        var user: User = User()
        if (userid != null && email != null && username != null && role != null) {
            user = User(id = userid, email = email, username = username, role = role)
        }
        val expirationTime = sharedPreferences.getLong("is_logged_in", 0L)

        // Check if the token is expired using the helper function `isTokenExpired`
        if (isTokenExpired(expirationTime)) {
            // Return null if the token is expired or about to expire
            return null
        }
        return user
    }

    fun getToken(): String?{
        val token = sharedPreferences.getString("token", null)

        return token
    }

    fun getTokenExpirationTime(): Long {
        val expirationTime = sharedPreferences.getLong("is_logged_in", 0L)

        return expirationTime
    }


    // Clear user data from shared preferences (e.g., on logout)

    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun clearUser() {
        // Create an editor to modify shared preferences
        val editor = sharedPreferences.edit()

        // Remove user ID, username, and email from shared preferences
        editor.remove("userid")
        editor.remove("username")
        editor.remove("email")
        editor.remove("is_logged_in")

        // Apply changes asynchronously
        editor.apply()
    }

    fun isTokenExpired(expirationTime: Long): Boolean {
        // Check if the current time has passed the expiration time (i.e., the token is expired)
        return System.currentTimeMillis() >= expirationTime
    }

    // Companion object for singleton instance management
    companion object {
        // This object was adapted from stackoverflow
        // https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
        // aminography
        // https://stackoverflow.com/users/1631967/aminography
        @Volatile
        private var INSTANCE: LocalUser? = null

        // Get the singleton instance of UserManager
        fun getInstance(context: Context): LocalUser {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalUser(context).also { INSTANCE = it }
            }
        }
    }

}