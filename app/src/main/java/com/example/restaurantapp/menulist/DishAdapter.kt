package com.example.restaurantapp.menulist

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R
import com.example.restaurantapp.database.Dish
import com.google.android.material.button.MaterialButton


class DishAdapter(
    private val userId: Long,
    private val context: Context,
    private val viewModel: MenuListViewModel,
    private val showDish: (
        id: Long
    ) -> Unit
) :
    ListAdapter<Dish, DishAdapter.DishViewHolder>(DishDiffCallback()) {

    inner class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productPoster: ImageView = itemView.findViewById(R.id.dishPoster)
        val name: TextView = itemView.findViewById(R.id.dish_name)
        val price: TextView = itemView.findViewById(R.id.price)
        val description: TextView = itemView.findViewById(R.id.description)
        val addButton: MaterialButton = itemView.findViewById(R.id.add_button)

        init {
            addButton.setOnClickListener {
                if (userId == -100L){
                    viewModel.editDish(getItem(adapterPosition).id, context)
                }else {
                    viewModel.addToBasket(context, userId, getItem(adapterPosition))
                }
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val id = getItem(position).id
                    showDish(
                        id
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.dish_list_item, parent, false)
        return DishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        if (userId == -100L){
            holder.addButton.icon = context.getDrawable(R.drawable.edit_icon)
        }
        holder.name.text = getItem(position)?.name
        holder.price.text = getItem(position)?.price.toString()
        holder.description.text = getItem(position)?.description
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