package com.example.restaurantapp.database

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream


class Converters {

    @TypeConverter
    fun fromDishList(dishes: List<Dish>): String {
        return Gson().toJson(dishes)
    }

    @TypeConverter
    fun toDishList(dishesString: String): List<Dish> {
        val listType = object : TypeToken<List<Dish>>() {}.type
        return Gson().fromJson(dishesString, listType)
    }
    @TypeConverter
    fun fromDrawable(drawable: Drawable?): ByteArray? {
        if (drawable == null) return null

        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}