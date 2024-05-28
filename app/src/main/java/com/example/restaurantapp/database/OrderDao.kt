package com.example.restaurantapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {
    @Query("SELECT * FROM [Order] WHERE userId = :userId")
    fun getOrdersListByUserId(userId: Long): List<Order>

    @Query("SELECT * FROM [Order] WHERE id = :id AND userId = :userId")
    fun getOrderByUserId(id: Long, userId: Long): Order?

    @Query("SELECT * FROM [Order] WHERE id = :id")
    fun getOrderById(id: Long): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: Order)

    @Query("DELETE FROM [Order] WHERE id = :id AND userId = :userId")
    fun deleteOrder(id: Long, userId: Long)

    @Query("DELETE FROM [Order] WHERE userId = :userId")
    fun deleteAllOrders(userId: Long)

    @Query("SELECT COUNT(*) FROM [Order] WHERE userId = :userId")
    fun getOrderCount(userId: Long): Int

    @Update
    fun updateOrder(order: Order)

    @Query("SELECT * FROM [Order] WHERE completeState = 3 AND userId = :userId")
    fun getAllCompleteOrders(userId: Long): List<Order>

    @Query("SELECT * FROM [Order] WHERE completeState != 3 AND userId = :userId")
    fun getAllCurrentOrders(userId: Long): List<Order>

    @Query("SELECT * FROM [Order] WHERE completeState != 3")
    fun getAllCurrentOrdersForAdmin(): List<Order>

}
