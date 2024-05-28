package com.example.restaurantapp.account

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R
import com.example.restaurantapp.database.Dish
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup


class OrderAdapter(
) :
    ListAdapter<Dish, OrderAdapter.DishViewHolder>(DishDiffCallback()) {

    inner class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productPoster: ImageView = itemView.findViewById(R.id.dishPoster)
        val name: TextView = itemView.findViewById(R.id.dish_name)
        val price: TextView = itemView.findViewById(R.id.price)
        val description: TextView = itemView.findViewById(R.id.description)
        val quantityGroup: MaterialButtonToggleGroup = itemView.findViewById(R.id.quantity_group)
        val removeButton: Button = itemView.findViewById(R.id.remove)
        val quantity: Button = itemView.findViewById(R.id.quantity)
        val addButton: Button = itemView.findViewById(R.id.add)

        init {
            removeButton.visibility = View.GONE
            addButton.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.basket_list_item, parent, false)
        return DishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.name.text = getItem(position)?.name
        holder.price.text = getItem(position)?.price.toString()
        holder.description.text = getItem(position)?.description
        holder.quantity.text = getItem(position)?.quantity.toString()
        holder.productPoster.setImageDrawable(byteArrayToDrawable(getItem(position).image))
    }


    class DishDiffCallback : DiffUtil.ItemCallback<Dish>() {
        override fun areItemsTheSame(
            oldItem: Dish,
            newItem: Dish
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Dish,
            newItem: Dish
        ): Boolean {
            return oldItem == newItem
        }
    }

    private fun byteArrayToDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}



