package com.example.restaurantapp.database

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Dish",
    indices = [Index(value = ["name"], unique = true)]
)
data class Dish(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val type: String,
    var quantity: Int = 1,
    val price: Int,
    val image: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dish

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (type != other.type) return false
        if (quantity != other.quantity) return false
        if (price != other.price) return false
        return image.contentEquals(other.image)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + quantity.hashCode()
        result = 31 * result + price
        result = 31 * result + image.contentHashCode()
        return result
    }
}
