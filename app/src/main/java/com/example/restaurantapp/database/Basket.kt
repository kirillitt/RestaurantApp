package com.example.restaurantapp.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Basket",
)
data class Basket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val composition: MutableList<Dish> = mutableListOf(),
    val userId: Long
)