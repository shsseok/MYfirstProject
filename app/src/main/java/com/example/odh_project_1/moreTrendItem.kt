import android.os.Parcel
import android.os.Parcelable

data class moreTrendItem(
    val rank: Int,
    val productName: String,
    val productPrice: String,
    val productImageUrl: String,
    val productLink: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(rank)
        parcel.writeString(productName)
        parcel.writeString(productPrice)
        parcel.writeString(productImageUrl)
        parcel.writeString(productLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<moreTrendItem> {
        override fun createFromParcel(parcel: Parcel): moreTrendItem {
            return moreTrendItem(parcel)
        }

        override fun newArray(size: Int): Array<moreTrendItem?> {
            return arrayOfNulls(size)
        }
    }
}