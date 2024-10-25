package com.xbcoders.xbcad7319.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xbcoders.xbcad7319.R
import com.xbcoders.xbcad7319.api.model.Product

class ProductAdapter(
    private val productList: MutableList<Product>,
    private val itemClickListener: (Product) -> Unit // Lambda function for item click
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val productImage: ImageView = itemView.findViewById(R.id.product_img)


        fun bind(product: Product, itemClickListener: (Product) -> Unit) {
            // Bind data to the UI components
            productName.text = product.name // Assuming you have a name field
            productPrice.text = "R${product.price}" // Assuming you have a price field

            // Load image using Glide
            Glide.with(itemView.context)
                .load(product.imageUrl) // Load the image from the URL
                .placeholder(R.drawable.baseline_image_24) // Optional: Add a placeholder
                //.error(R.drawable.error_image) // Optional: Add an error image
                .into(productImage)

            // Set click listener for the entire item
            itemView.setOnClickListener { itemClickListener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product, itemClickListener)
    }

    override fun getItemCount(): Int = productList.size

    fun updateProducts(products: List<Product>){
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }
}
