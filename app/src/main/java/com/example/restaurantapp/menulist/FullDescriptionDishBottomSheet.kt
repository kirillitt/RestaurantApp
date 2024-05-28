package com.example.restaurantapp.menulist


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResult
import com.example.restaurantapp.R
import com.example.restaurantapp.database.AppDatabase
import com.example.restaurantapp.basket.BasketRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FullDescriptionDishBottomSheet : BottomSheetDialogFragment() {
    private lateinit var dishImage: ImageView
    private lateinit var dishName: TextView
    private lateinit var dishPrice: TextView
    private lateinit var dishDescription: TextView
    private lateinit var dishAdd: MaterialButton
    private lateinit var userIdPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.full_description_dish_bottom_sheet, container, false)
        context?.let {
            userIdPreferences = it.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        }
        dishImage = view.findViewById(R.id.dishPoster)
        dishName = view.findViewById(R.id.dish_name)
        dishPrice = view.findViewById(R.id.price)
        dishDescription = view.findViewById(R.id.description)
        dishAdd = view.findViewById(R.id.add_button)

        val dishId = arguments?.getLong("ID")
        CoroutineScope(Dispatchers.IO).launch {
            val dish = dishId?.let {
                context?.let { it1 ->
                    AppDatabase.getDatabase(it1).dishDao().getDishById(it)
                }
            }
            withContext(Dispatchers.Main) {
                dishImage.setImageDrawable(byteArrayToDrawable(dish?.image))
                dishName.text = dish?.name
                dishPrice.text = dish?.price.toString()
                dishDescription.text = dish?.description
            }
        }

        dishAdd.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val basketRepository = context?.let { it1 ->
                    AppDatabase.getDatabase(
                        it1
                    ).basketDao()
                }?.let { it2 -> BasketRepository(it2) }
                val dish = dishId?.let {
                    context?.let { it1 ->
                        AppDatabase.getDatabase(it1).dishDao().getDishById(it)
                    }
                }
                if (dish != null) {
                    basketRepository?.manageDishInBasket(
                        userIdPreferences.getLong("user_id", -1L),
                        dish, 1
                    )
                }
                withContext(Dispatchers.Main) {
                    setFragmentResult("dishAdd", Bundle())
                    dismiss()

                }
            }

        }

        return view
    }

    companion object {
        const val TAG = "FullDescriptionDishBottomSheet"
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}