package com.example.restaurantapp.account


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R
import com.example.restaurantapp.database.AppDatabase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FullDescriptionOrderBottomSheet : BottomSheetDialogFragment() {
    private lateinit var orderName: TextView
    private lateinit var orderState: TextView
    private lateinit var totalPrice: TextView
    private lateinit var categoryCompleted: TextInputLayout
    private lateinit var chosenCompleted: AutoCompleteTextView
    private lateinit var orderComposition: RecyclerView
    private lateinit var userIdPreferences: SharedPreferences
    private lateinit var saveButton: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val orderId = arguments?.getLong("ID")
        val view = inflater.inflate(R.layout.full_description_order_bottom_sheet, container, false)
        context?.let {
            userIdPreferences = it.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        }
        val userId = userIdPreferences.getLong("user_id", -1L)
        orderName = view.findViewById(R.id.order_name)
        orderState = view.findViewById(R.id.order_state)
        totalPrice = view.findViewById(R.id.total_price)
        orderComposition = view.findViewById(R.id.order_composition)
        chosenCompleted = view.findViewById(R.id.chosen_completed)
        categoryCompleted = view.findViewById(R.id.category_completed)
        saveButton = view.findViewById(R.id.save_button)
        val adapter = OrderAdapter()
        if (userId == -100L) {
            orderState.visibility = View.GONE
            categoryCompleted.visibility = View.VISIBLE
            chosenCompleted.visibility = View.VISIBLE
            saveButton.visibility = View.VISIBLE
        } else {
            orderState.visibility = View.VISIBLE
            categoryCompleted.visibility = View.GONE
            chosenCompleted.visibility = View.GONE
            saveButton.visibility = View.GONE
        }
        val orderStates = resources.getStringArray(R.array.categories_of_completed)
        val adapterState = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, orderStates)
        chosenCompleted.setAdapter(adapterState)

        CoroutineScope(Dispatchers.IO).launch {
            val order = orderId?.let {
                context?.let { it1 ->
                    if (userId == -100L) {
                        AppDatabase.getDatabase(it1).orderDao().getOrderById(orderId)
                    } else {
                        AppDatabase.getDatabase(it1).orderDao().getOrderByUserId(orderId, userId)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                orderName.text = order?.name
                if (userId == -100L) {
                    val stateOrder = when (order?.completeState) {
                        0 -> "Заказ формируется"
                        1 -> "Заказ готовится"
                        2 -> "Заказ доставляется"
                        3 -> "Заказ доставлен"
                        else -> "Ошибка"
                    }
                    chosenCompleted.setText(stateOrder, false)
                } else {
                    orderState.text = when (order?.completeState) {
                        0 -> "Заказ формируется"
                        1 -> "Заказ готовится"
                        2 -> "Заказ доставляется"
                        3 -> "Заказ доставлен"
                        else -> "Ошибка"
                    }
                }

                totalPrice.text = buildString {
                    append(order?.composition?.sumOf { dish -> dish.price * dish.quantity }.toString())
                    append(" Руб.")
                }
                orderComposition.adapter = adapter
                orderComposition.layoutManager = LinearLayoutManager(context)
                adapter.submitList(order?.composition)
                saveButton.setOnClickListener {
                    val stateOrderFinal: Int = when (chosenCompleted.text.toString()) {
                        "Заказ формируется" -> 0
                        "Заказ готовится" -> 1
                        "Заказ доставляется" -> 2
                        "Заказ доставлен" -> 3
                        else ->
                            4
                    }
                    val orderFinal = order?.copy(completeState = stateOrderFinal)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (orderFinal != null) {
                            context?.let { it1 ->
                                AppDatabase.getDatabase(it1).orderDao().updateOrder(orderFinal)
                            }
                        }
                        withContext(Dispatchers.Main){
                            setFragmentResult("orderUpdated", Bundle())
                            dismiss()
                        }
                    }

                }
            }
        }
        return view
    }
    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    companion object {
        const val TAG = "FullDescriptionOrderBottomSheet"
    }
}