package com.example.restaurantapp.menulist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R
import com.example.restaurantapp.basket.BasketAdapter
import com.example.restaurantapp.basket.BasketViewModel
import com.example.restaurantapp.database.AppDatabase
import com.example.restaurantapp.database.Dish
import com.example.restaurantapp.database.Order
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateOrEditDishActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var dishNameLayout: TextInputLayout
    private lateinit var dishNameEditText: TextInputEditText
    private lateinit var dishDescriptionLayout: TextInputLayout
    private lateinit var dishDescriptionEditText: TextInputEditText
    private lateinit var dishTypeLayout: TextInputLayout
    private lateinit var dishTypeEditText: TextInputEditText
    private lateinit var dishPriceLayout: TextInputLayout
    private lateinit var dishPriceEditText: TextInputEditText
    private lateinit var dishImageView: ImageView
    private lateinit var saveEditButton: MaterialButton
    private lateinit var preferences: SharedPreferences
    private lateinit var userIdPreferences: SharedPreferences

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                dishImageView.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_or_edit_dish_activity)
        val action = intent.getStringExtra("CreateOrEdit")
        val dishId = intent.getLongExtra("ID", -1L)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        userIdPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = userIdPreferences.getLong("user_id", -1L)
        toolbar = findViewById(R.id.topAppBar)
        dishNameLayout = findViewById(R.id.dish_name)
        dishNameEditText = findViewById(R.id.dish_name_text)
        dishDescriptionLayout = findViewById(R.id.dish_description)
        dishDescriptionEditText = findViewById(R.id.dish_description_text)
        dishTypeLayout = findViewById(R.id.dish_type)
        dishTypeEditText = findViewById(R.id.dish_type_text)
        dishPriceLayout = findViewById(R.id.dish_price)
        dishPriceEditText = findViewById(R.id.dish_price_text)
        dishImageView = findViewById(R.id.image_dish)
        saveEditButton = findViewById(R.id.save_dish_button)


        if (action == "Edit") {
            CoroutineScope(Dispatchers.IO).launch {
                val dish = AppDatabase.getDatabase(this@CreateOrEditDishActivity).dishDao()
                    .getDishById(dishId)
                withContext(Dispatchers.Main) {
                    saveEditButton.text = "Сохранить изменения"
                    dishNameEditText.setText(dish.name)
                    dishDescriptionEditText.setText(dish.description)
                    dishTypeEditText.setText(dish.type)
                    dishPriceEditText.setText(dish.price.toString())
                    dishImageView.setImageDrawable(byteArrayToDrawable(dish.image))
                }
            }
        }

        dishImageView.setOnClickListener {
            pickImageFromGallery()
        }

        saveEditButton.setOnClickListener {
            if (isFieldsNotEmpty()) {
                val dish = Dish(
                    name = dishNameEditText.text.toString(),
                    description = dishDescriptionEditText.text.toString(),
                    type = dishTypeEditText.text.toString(),
                    price = dishPriceEditText.text.toString().toInt(),
                    image = drawableToByteArray(dishImageView.drawable)
                )
                CoroutineScope(Dispatchers.IO).launch {
                    if (action == "Edit") {
                        AppDatabase.getDatabase(this@CreateOrEditDishActivity).dishDao()
                            .updateDish(dish.copy(id = dishId))
                    } else  if (action == "Create") {
                        AppDatabase.getDatabase(this@CreateOrEditDishActivity).dishDao()
                            .insertDish(dish)
                    }
                    withContext(Dispatchers.Main) {
                        val intent =
                            Intent(this@CreateOrEditDishActivity, MenuListActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(this, "Заполните поля!", Toast.LENGTH_SHORT).show()
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    if (dishId != -1L) {
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.getDatabase(this@CreateOrEditDishActivity).dishDao()
                                .deleteDish(dishId)
                        }
                        val intent = Intent(this, MenuListActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@CreateOrEditDishActivity,
                            "Нельзя удалить несозданный продукт!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    true
                }

                else -> false
            }
        }
    }

    private fun isFieldsNotEmpty(): Boolean {
        return dishNameEditText.text?.isNotBlank() == true && dishDescriptionEditText.text?.isNotBlank() == true &&
                dishTypeEditText.text?.isNotBlank() == true && dishPriceEditText.text?.isNotBlank() == true
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }

    private fun drawableToByteArray(drawable: Drawable): ByteArray {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        } else {
            throw IllegalArgumentException("Drawable must be instance of BitmapDrawable")
        }
    }

    private fun pickImageFromGallery() {
        pickImage.launch("image/*")
    }
}