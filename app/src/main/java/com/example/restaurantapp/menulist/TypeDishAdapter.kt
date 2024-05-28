package com.example.restaurantapp.menulist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp.R


class TypeDishAdapter(
    private val context: Context,
    private val viewModel: MenuListViewModel
) :
    ListAdapter<String, TypeDishAdapter.TypeViewHolder>(TypeDiffCallback()) {

    inner class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeDish: TextView = itemView.findViewById(R.id.type)

        init {
            typeDish.setOnClickListener {
                viewModel.chooseTypeOfDishes(context, getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.type_dish_list_item, parent, false)
        return TypeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.typeDish.text = getItem(position)
    }


    class TypeDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}