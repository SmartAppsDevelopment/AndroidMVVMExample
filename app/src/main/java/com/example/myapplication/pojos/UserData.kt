package com.example.myapplication.pojos

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(primaryKeys = ["name","country_id"])
@Parcelize
data class UserData(
    @ColumnInfo(defaultValue = "0")val age: String="",
    val count: Int=0,
    val name: String=""
):Parcelable{
    var userImageRef:String=""
    var country_id:String=""
}