package com.example.odh_project_1

import android.os.Parcel
import android.os.Parcelable

data class moreNewsItem(
    val title: String,
    val link: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<moreNewsItem> {
        override fun createFromParcel(parcel: Parcel): moreNewsItem {
            return moreNewsItem(parcel)
        }

        override fun newArray(size: Int): Array<moreNewsItem?> {
            return arrayOfNulls(size)
        }
    }
}