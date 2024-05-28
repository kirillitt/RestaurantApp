package com.example.restaurantapp.basket

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapp.database.AppDatabase
import com.example.restaurantapp.database.Basket
import com.example.restaurantapp.database.Dish
import com.example.restaurantapp.database.Order
import com.example.restaurantapp.database.User
import com.example.restaurantapp.menulist.MenuListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasketViewModel(
    context: Context,
    id: Long
) : ViewModel() {

    private val userId = id
    private val basketRepository = BasketRepository(AppDatabase.getDatabase(context).basketDao())

    private val _basket = MutableLiveData<List<Dish>>()
    val basket: LiveData<List<Dish>> get() = _basket

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _error = MutableLiveData<String>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _basket.postValue(
                AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)?.composition
            )
            _user.postValue(AppDatabase.getDatabase(context).userDao().getUserById(userId))
            Log.i("userA1", _user.value.toString())
        }
    }

    fun createOrder(context: Context, order: Order, user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(context).orderDao().insertOrder(order)
            AppDatabase.getDatabase(context).userDao().updateUser(user)
            val emptyBasket = AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)
            emptyBasket?.copy(composition = mutableListOf())
                ?.let { AppDatabase.getDatabase(context).basketDao().updateBasket(it) }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Заказ оформлен! Ослеживайте его в личном кабинете",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(context, MenuListActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    fun deleteBasket(userId: Long, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val basket = AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)
            basket?.copy(composition = mutableListOf())
                ?.let { AppDatabase.getDatabase(context).basketDao().updateBasket(it) }
        }
    }

    fun updateBasket(context: Context, dish: Dish, action: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            basketRepository.manageDishInBasket(userId, dish, action)
            _basket.postValue(
                AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)?.composition
            )
        }
    }
}