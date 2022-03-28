package com.example.myapplication.datauimodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector

val listOfBottomNavMenu= mutableListOf(BottomNavDataModel.item1,BottomNavDataModel.item2)
sealed class BottomNavDataModel(val img:ImageVector,val text:String,val isSelected:Boolean=false) {
    object item1:BottomNavDataModel(Icons.Filled.Phone,"Phone", isSelected = true)
    object item2:BottomNavDataModel(Icons.Filled.AccountBox,"AccountBox")
}