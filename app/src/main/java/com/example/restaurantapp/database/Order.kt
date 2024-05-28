package com.example.restaurantapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Order",
    indices = [Index(value = ["name"], unique = true)]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String?,
    val composition: List<Dish>,
    val completeState: Int = 0,
    val userId: Long
)