package com.example.restaurantapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "User",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String,
    val phone: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val patronymic: String? = null,
    val hotelRoom: String? = null,
    val isAdmin: Boolean = false
)