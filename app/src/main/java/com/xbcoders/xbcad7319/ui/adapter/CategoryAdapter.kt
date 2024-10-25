package com.xbcoders.xbcad7319.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xbcoders.xbcad7319.R

class CategoryAdapter (
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = -1 // Track the currently selected position

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)

        init {
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition // Update selected position

                // Notify the adapter to refresh the views
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Trigger the callback with the selected category
                onCategorySelected(categories[selectedPosition])
            }
        }

        fun bind(category: String) {
            categoryName.text = category

            // Change background color based on selection
//            itemView.setBackgroundColor(
//                if (adapterPosition == selectedPosition)
//                    ContextCompat.getColor(itemView.context, R.color.primary)
//                else
//                    ContextCompat.getColor(itemView.context, android.R.color.transparent) // Default background
//            )

            itemView.setBackgroundResource(
                if (adapterPosition == selectedPosition)
                    R.color.primary // Use color resource for selected position
                else
                    R.drawable.edit_text_background // Use drawable for default background
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}