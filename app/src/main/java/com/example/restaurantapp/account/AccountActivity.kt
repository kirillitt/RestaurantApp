package com.example.restaurantapp.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.menulist.MenuListActivity
import com.example.restaurantapp.R
import com.example.restaurantapp.authentication.AuthActivity
import com.example.restaurantapp.database.AppDatabase
import com.example.restaurantapp.menulist.FullDescriptionDishBottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountActivity : AppCompatActivity() {

    private lateinit var completedOrdersList: RecyclerView
    private lateinit var currentOrdersList: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var accountName: TextView
    private lateinit var completedOrdersTitle: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var viewModel: AccountViewModel
    private lateinit var adapterCurrentOrders: CurrentOrderAdapter
    private lateinit var userIdPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_activity)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        completedOrdersList = findViewById(R.id.completed_orders_list)
        currentOrdersList = findViewById(R.id.current_orders_list)
        completedOrdersTitle = findViewById(R.id.completed_orders_title)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        accountName = findViewById(R.id.account_name)
        logoutButton = findViewById(R.id.logout_button)
        val userId = userIdPreferences.getLong("user_id", -1L)
        viewModel = AccountViewModel(this, userId)

        if (userId == -100L) {
            completedOrdersList.visibility = View.GONE
            completedOrdersTitle.visibility = View.GONE
        }
        adapterCurrentOrders = CurrentOrderAdapter(::showDish)


        val adapterCompletedOrders = CompletedOrdersAdapter(::showDish)
        completedOrdersList.layoutManager = LinearLayoutManager(this)
        currentOrdersList.layoutManager = LinearLayoutManager(this)
        completedOrdersList.adapter = adapterCompletedOrders
        currentOrdersList.adapter = adapterCurrentOrders


        viewModel.completedOrders.observe(this) { completedOrders ->
            adapterCompletedOrders.submitList(completedOrders)
        }
        viewModel.currentOrders.observe(this) { currentOrders ->
            adapterCurrentOrders.submitList(currentOrders)
        }
        viewModel.user.observe(this) { user ->
            accountName.text = user.name
        }
        supportFragmentManager.setFragmentResultListener(
            "orderUpdated",
            this,
            FragmentResultListener { requestKey, result ->
                if (requestKey == "orderUpdated") {
                    viewModel.updateOrderList(this)
                }
            })
        bottomNavigation.selectedItemId = R.id.account
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu -> {
                    val intent = Intent(this, MenuListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.account -> {
                    true
                }

                R.id.support -> {
                    viewModel.support(this)
                    true
                }

                else -> false
            }
        }
        logoutButton.setOnClickListener {
            userIdPreferences.edit().clear().apply()
            Log.i("IdUser", userIdPreferences.getLong("user_id", -1L).toString())
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDish(id: Long) {
        val bottomSheetDialog = FullDescriptionOrderBottomSheet()
        val args = Bundle()
        args.putLong("ID", id)
        bottomSheetDialog.arguments = args
        bottomSheetDialog.show(supportFragmentManager, "FullDescriptionOrderBottomSheet")
    }
}
