package com.xbcoders.xbcad7319.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.model.CartItem
import com.xbcoders.xbcad7319.ui.CartAction

class CartItemAdapter(
    private var cartItems: MutableList<CartItem>,
    private val itemActionListener: (CartItem, CartAction) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val quantityText: TextView = itemView.findViewById(R.id.quantity_text)
        private val increaseImage: ImageView = itemView.findViewById(R.id.increase_image)
        private val decreaseImage: ImageView = itemView.findViewById(R.id.decrease_image)
        private val productImg: ImageView = itemView.findViewById(R.id.product_image_view)
        fun bind(cartItem: CartItem) {
            productName.text = cartItem.product.name // Assuming Product has a productName property
            quantityText.text = cartItem.quantity.toString()

            increaseImage.setOnClickListener {
                itemActionListener(cartItem, CartAction.INCREASE)
            }

            if (cartItem.quantity == 1) {
                // Show the bin icon for removal
                decreaseImage.setImageResource(R.drawable.baseline_delete_24) // Replace with your bin icon drawable
                decreaseImage.setOnClickListener {
                    itemActionListener(cartItem, CartAction.REMOVE)
                }
            } else {
                // Show the minus icon for decreasing quantity
                decreaseImage.setImageResource(R.drawable.ic_remove) // Replace with your minus icon drawable
                decreaseImage.setOnClickListener {
                    itemActionListener(cartItem, CartAction.DECREASE)
                }
            }

            // Load image using Glide
            Glide.with(itemView.context)
                .load(cartItem.product.imageUrl) // Load the image from the URL
                .placeholder(R.drawable.baseline_image_24) // Optional: Add a placeholder
                //.error(R.drawable.error_image) // Optional: Add an error image
                .into(productImg)

            itemView.setOnClickListener {
                itemActionListener(cartItem, CartAction.VIEW)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeCartItem(cartItemId: String) {
        cartItems.removeAll { it.id == cartItemId }
        notifyDataSetChanged()
    }
}

