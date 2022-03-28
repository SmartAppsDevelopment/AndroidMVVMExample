package com.example.myapplication.component

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.datauimodel.listOfBottomNavMenu


@Composable
fun GoogleBottomNavigation() {
    BottomNavigation(backgroundColor = Color.White, elevation = 10.dp) {
        listOfBottomNavMenu.forEach {
            BottomNavigationItem(
                selected = it.isSelected,
                onClick = { },
                icon = { Icon(it.img,"" )},
                label = { Text(text = it.text) })
        }
    }

}