package com.example.ui.base.helper

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph

 class SampleNavigator(context: Context) : NavController(context) {
    override fun navigate(resId: Int) {
//        super.navigate(resId)
        commonNavigation(resId)
    }

    override fun navigate(directions: NavDirections) {
        commonNavigation(directions.actionId)
        //  super.navigate(directions)
    }

    private fun commonNavigation(resID:Int){
        val destinationId = currentDestination?.getAction(resID)?.destinationId ?: 0
        currentDestination?.let { node ->
            val currentNode = when (node) {
                is NavGraph -> node
                else -> node.parent
            }
            if (destinationId != 0) {
                currentNode?.findNode(destinationId)?.let { navigate(resID) }
            }
        }
    }
}