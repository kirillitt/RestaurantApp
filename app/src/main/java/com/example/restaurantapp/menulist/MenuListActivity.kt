package com.example.restaurantapp.menulist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.basket.BasketActivity
import com.example.restaurantapp.R
import com.example.restaurantapp.account.AccountActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MenuListActivity : AppCompatActivity() {

    private lateinit var menuList: RecyclerView
    private lateinit var typeDishesRecycler: RecyclerView
    private lateinit var viewModel: MenuListViewModel
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var basketButton: MaterialButton
    private lateinit var addButton: FloatingActionButton
    private lateinit var userIdPreferences: SharedPreferences
    private var userId = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_list_activity)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = userIdPreferences.getLong("user_id", -1L)
        menuList = findViewById(R.id.menu_list)
        typeDishesRecycler = findViewById(R.id.type_dishes_list)
        basketButton = findViewById(R.id.basket_button)
        addButton = findViewById(R.id.add_button)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        Log.i("asdasdasd", userId.toString())
        viewModel = MenuListViewModel(this, userId)
        val adapterMenu = DishAdapter(userId, this, viewModel, ::showDish)
        val adapterType = TypeDishAdapter(this, viewModel)
        if (userId == -100L) {
            addButton.visibility = View.VISIBLE
        }
        menuList.adapter = adapterMenu
        typeDishesRecycler.adapter = adapterType
        menuList.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        typeDishesRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bottomNavigation.selectedItemId = R.id.menu
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu -> {
                    true
                }

                R.id.account -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.support -> {
                    viewModel.support(this)
                    true
                }

                else -> false
            }
        }
        basketButton.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        addButton.setOnClickListener {
            val intent = Intent(this, CreateOrEditDishActivity::class.java)
            intent.putExtra("CreateOrEdit", "Create")
            startActivity(intent)

        }
        viewModel.dishes.observe(this) { listDishes ->
            adapterMenu.submitList(listDishes)
        }

        viewModel.typedishes.observe(this) { listTypes ->
            adapterType.submitList(listTypes)
        }
        viewModel.basket.observe(this) { basket ->
            if (userId != -100L) {
                if (basket.composition.isEmpty()) {
                    basketButton.visibility = View.GONE
                } else {
                    basketButton.visibility = View.VISIBLE
                }
            }
        }
        supportFragmentManager.setFragmentResultListener(
            "dishAdd",
            this,
            FragmentResultListener { requestKey, result ->
                if (requestKey == "dishAdd") {
                    viewModel.updateBasket(userId, this)
                }
            })

    }

    private fun showDish(
        id: Long
    ) {
        val bottomSheetDialog = FullDescriptionDishBottomSheet()
        val args = Bundle()
        args.putLong("ID", id)
        bottomSheetDialog.arguments = args
        bottomSheetDialog.show(supportFragmentManager, "FullReviewBottomSheet")
    }
}