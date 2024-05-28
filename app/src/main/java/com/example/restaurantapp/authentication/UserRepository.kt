package com.example.restaurantapp.authentication

import android.content.Context
import android.util.Log
import com.example.restaurantapp.database.AppDatabase
import com.example.restaurantapp.database.Basket
import com.example.restaurantapp.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    suspend fun login(username: String, password: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val user = AppDatabase.getDatabase(context).userDao().getUserByUsername(username)
            Log.i("userAdmin", user.toString())
            user?.password == password
        }
    }

    suspend fun register(user: User, context: Context): User? {
        return withContext(Dispatchers.IO) {
            val existingUser = AppDatabase.getDatabase(context).userDao().getUserByUsername(user.username)
            if (existingUser == null) {
                // Insert the new user
                AppDatabase.getDatabase(context).userDao().insertUser(user)

                // Get the newly created user
                val newUser = AppDatabase.getDatabase(context).userDao().getUserByUsername(user.username)

                // Create a new basket for the new user
                newUser?.let {
                    val basket = Basket(userId = it.id)
                    AppDatabase.getDatabase(context).basketDao().insertBasket(basket)
                }

                newUser
            } else {
                null // User already exists
            }
        }
    }
}