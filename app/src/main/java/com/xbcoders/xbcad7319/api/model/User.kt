package com.xbcoders.xbcad7319.api.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    var id: String = "",               // Unique identifier for the user
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var balance: Double = 0.0,
    var role: String = "user",
    val createdAt: Long = 0L,
    var token: String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        username = parcel.readString() ?: "",
        password = parcel.readString() ?: "",
        balance = parcel.readDouble(),
        role = parcel.readString() ?: "user",
        createdAt = parcel.readLong(),
        token = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeDouble(balance)
        parcel.writeString(role)
        parcel.writeLong(createdAt)
        parcel.writeString(token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

