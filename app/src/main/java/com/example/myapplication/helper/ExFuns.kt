package com.example.myapplication.helper

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

suspend fun <T> Flow<List<T>>.flattenToList() =
    flatMapConcat { it.asFlow() }.toList()


inline fun <reified T> T.showLog(msg: String) {
    Log.e(T::class.simpleName, msg)
}

inline fun <reified T : Context> T.getResString(id: Int): String = resources.getString(id)

//fun NavController.navigateSafe(resId: Int, args: Bundle? = null) {
//    val destinationId = currentDestination?.getAction(resId)?.destinationId.orEmpty()
//    currentDestination?.let { node ->
//        val currentNode = when (node) {
//            is NavGraph -> node
//            else -> node.parent
//        }
//        if (destinationId != 0) {
//            currentNode?.findNode(destinationId)?.let { navigate(resId, args) }
//        }
//    }
//}
//fun NavController.navigateSafe(resId: NavDirections) {
//    val destinationId = currentDestination?.getAction(resId.actionId)?.destinationId.orEmpty()
//    currentDestination?.let { node ->
//        val currentNode = when (node) {
//            is NavGraph -> node
//            else -> node.parent
//        }
//        if (destinationId != 0) {
//            currentNode?.findNode(destinationId)?.let { navigate(resId) }
//        }
//    }
//}
public fun Int?.orEmpty(default: Int = 0): Int {
    return this ?: default
}
fun NavController.navigateSafe(navDirections: NavDirections) {
    val destinationId = currentDestination?.getAction(navDirections.actionId)?.destinationId.orEmpty()
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        if (destinationId != 0) {
            currentNode?.findNode(destinationId)?.let { navigate(navDirections) }
        }
    }}
