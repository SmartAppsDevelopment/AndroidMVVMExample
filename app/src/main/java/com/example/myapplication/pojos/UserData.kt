package com.example.myapplication.pojos

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Entity(primaryKeys = ["name","countryId"])
@Parcelize
data class UserData(
    @ColumnInfo(defaultValue = "0")val age: String="",
    val count: Int=0,
    val name: String=""
):Parcelable{
    @IgnoredOnParcel
    var userImageRef:String=""
    @SerializedName("country_id")
    var countryId:String=""
}