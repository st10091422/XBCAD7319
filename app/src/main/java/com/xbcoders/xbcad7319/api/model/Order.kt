package com.xbcoders.xbcad7319.api.model

import android.os.Parcel
import android.os.Parcelable

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val status: String = "Pending",
    val orderNo: String = "",
    val createdAt: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        userId = parcel.readString() ?: "",
        items = parcel.createTypedArrayList(CartItem) ?: emptyList(),
        totalPrice = parcel.readDouble(),
        address = parcel.readString() ?: "",
        status = parcel.readString() ?: "Pending",
        orderNo = parcel.readString() ?: "",
        createdAt = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeTypedList(items)
        parcel.writeDouble(totalPrice)
        parcel.writeString(address)
        parcel.writeString(status)
        parcel.writeString(orderNo)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
