package com.example.myapplication.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList

fun Context.showToast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}

suspend fun <T> Flow<List<T>>.flattenToList() =
    flatMapConcat { it.asFlow() }.toList()


inline fun <reified T> T.showLog(msg : String){
    Log.e(T::class.simpleName, msg)
}
inline fun <reified T:Context> T.getResString(id:Int):String=resources.getString(id)
