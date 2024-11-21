package com.xbcoders.xbcad7319.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.api.model.Order
import com.xbcoders.xbcad7319.databinding.ItemOrderBinding
import com.xbcoders.xbcad7319.ui.CartAction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter (private val onItemClick: (Order) -> Unit) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private val orders: MutableList<Order> = mutableListOf()
    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {

            binding.productName.text = order.orderNo
            binding.quantity.text = "${order.items.size} items"
            binding.date.text = formatOrderDate(order.createdAt)

            if (order.status == "Pending") {
                binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.yellow))
            } else if (order.status == "Delivered") {
                binding.status.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green)) // Example color for Delivered status
            }


            binding.status.text = order.status

            // You can also bind order items here if needed

            itemView.setOnClickListener {
                onItemClick(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    fun formatOrderDate(createdAt: Long): String {
        val date = Date(createdAt)
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        // Insert a newline between the day and the month
        return formattedDate.replace(" ", "\n")
    }

    fun updateOrders(data: List<Order>) {
        orders.clear()
        orders.addAll(data)
        notifyDataSetChanged()
    }
}