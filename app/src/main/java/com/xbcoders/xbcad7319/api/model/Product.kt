package com.xbcoders.xbcad7319.api.model

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String = "",               // Unique identifier for the product
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val quantity: Int = 0,
    val imageUrl: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        price = parcel.readDouble(),
        category = parcel.readString() ?: "",
        quantity = parcel.readInt(),
        imageUrl = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeDouble(price)
        parcel.writeString(category)
        parcel.writeInt(quantity)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

