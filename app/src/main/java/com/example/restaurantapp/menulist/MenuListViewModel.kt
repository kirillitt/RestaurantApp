package com.example.restaurantapp.menulist

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapp.database.AppDatabase
import com.example.restaurantapp.database.Basket
import com.example.restaurantapp.basket.BasketRepository
import com.example.restaurantapp.database.Dish
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MenuListViewModel(
    context: Context,
    userId: Long
) : ViewModel() {

    private val basketRepository = BasketRepository(AppDatabase.getDatabase(context).basketDao())

    private val _dishes = MutableLiveData<List<Dish>>()
    val dishes: LiveData<List<Dish>> get() = _dishes

    private val _typedishes = MutableLiveData<List<String>>()
    val typedishes: LiveData<List<String>> get() = _typedishes

    private val _basket = MutableLiveData<Basket>()
    val basket: LiveData<Basket> get() = _basket

    private val _error = MutableLiveData<String>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _dishes.postValue(AppDatabase.getDatabase(context).dishDao().getAllDishes())
            _basket.postValue(
                AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)
            )
            val listType = AppDatabase.getDatabase(context).dishDao().getAllDishTypes()
            withContext(Dispatchers.IO) {
                val list = mutableListOf<String>()
                list.add("Все")
                list.addAll(listType)
                _typedishes.postValue(list)
            }
        }
    }

    fun support(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Возникли вопросы?")
            .setMessage("Свяжитесь с администратором по номеру: \n +79648248373")
            .setPositiveButton("Ок") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun chooseTypeOfDishes(context: Context, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (type == "Все") {
                _dishes.postValue(AppDatabase.getDatabase(context).dishDao().getAllDishes())
            } else {
                _dishes.postValue(AppDatabase.getDatabase(context).dishDao().getDishesByType(type))
                Log.i("typetype", _dishes.value?.size.toString())
            }
        }
    }

    fun updateBasket(userId: Long, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            _basket.postValue(
                AppDatabase.getDatabase(context).basketDao().getBasketByUserId(userId)
            )
        }
    }

    fun addToBasket(context: Context, userId: Long, dish: Dish) {
        CoroutineScope(Dispatchers.IO).launch {
            basketRepository.manageDishInBasket(userId, dish, 1)
            updateBasket(userId, context)
        }
    }

    fun editDish(dishId: Long, context: Context) {
        val intent = Intent(context, CreateOrEditDishActivity::class.java)
        intent.putExtra("ID", dishId)
        intent.putExtra("CreateOrEdit", "Edit")
        context.startActivity(intent)
    }
}

