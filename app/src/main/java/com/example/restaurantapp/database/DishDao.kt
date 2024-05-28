package com.example.restaurantapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DishDao {

    @Query("SELECT * FROM Dish WHERE id = :id")
    fun getDishById(id: Long): Dish

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDish(dish: Dish)

    @Query("DELETE FROM Dish WHERE id = :id")
    fun deleteDish(id: Long)

    @Query("SELECT * FROM Dish")
    fun getAllDishes(): List<Dish>

    @Query("DELETE FROM Dish")
    fun deleteAllDishes()

    @Query("SELECT COUNT(*) FROM Dish")
    fun getDishesCount(): Int

    @Update
    fun updateDish(dish: Dish)

    @Query("SELECT * FROM Dish WHERE type = :type")
    fun getDishesByType(type: String): List<Dish>

    @Query("SELECT DISTINCT type FROM Dish")
    fun getAllDishTypes(): List<String>
}