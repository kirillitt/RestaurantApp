package com.example.restaurantapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.restaurantapp.database.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE username = :username")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM User")
    fun getAllUser(): List<User?>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUser(user: User)

    @Query("DELETE FROM User WHERE id = :id")
    fun deleteUser(id: Long)

    @Update
    fun updateUser(user: User)
}