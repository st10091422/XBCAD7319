package com.xbcoders.xbcad7319.api.model

import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    val id: String = "",               // Unique identifier for the cart item
    var productId: String = "",
    var quantity: Int = 1,
    var userId: String = "",
    var product: Product = Product()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        productId = parcel.readString() ?: "",
        quantity = parcel.readInt(),
        userId = parcel.readString() ?: "",
        product = parcel.readParcelable(Product::class.java.classLoader) ?: Product() // Read product from parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(productId)
        parcel.writeInt(quantity)
        parcel.writeString(userId)
        parcel.writeParcelable(product, flags) // Write product to parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}
