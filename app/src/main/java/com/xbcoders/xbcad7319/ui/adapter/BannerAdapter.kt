package com.xbcoders.xbcad7319.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.xbcoders.xbcad7319.R

/**
 * Adapter for displaying banner images in a ViewPager2.
 */
class BannerAdapter(private val bannerImages: List<Int>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    /**
     * ViewHolder for a single banner item.
     */
    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get a reference to the ImageView in the banner item layout
        val imageView: ImageView = itemView.findViewById(R.id.bannerImageView)
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new BannerViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        // Inflate the layout for the banner item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        // Return a new BannerViewHolder with the inflated view
        return BannerViewHolder(view)
    }

    /**
     * Binds data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the adapter.
     */
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        // Set the image resource for the ImageView in the ViewHolder
        holder.imageView.setImageResource(bannerImages[position])
    }

    /**
     * Returns the total number of items in the adapter.
     *
     * @return The total number of items.
     */
    override fun getItemCount(): Int = bannerImages.size
}