package com.example.restaurantapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BasketDao {
    @Query("SELECT * FROM Basket WHERE userId = :userId")
    fun getBasketByUserId(userId: Long): Basket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBasket(basket: Basket)

    @Query("DELETE FROM Basket WHERE userId = :userId")
    fun deleteBasket(userId: Long)

    @Update
    fun updateBasket(basket: Basket)

}
