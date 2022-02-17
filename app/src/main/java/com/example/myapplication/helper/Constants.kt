package com.example.myapplication.helper

import android.content.Context
import java.io.File


object FileRef{
     private fun getBaseFile(context: Context): File {
       val ff= File( context.filesDir,"AppBaseDir")
         if(!ff.exists())
             ff.mkdir()
         return ff
    }
     fun getBaseFileForImage(context: Context): File {
        return File( getBaseFile(context),"IMG_"+String.format("%5d",(1..10000).random())+".jpg")
    }
}
enum class DataSourceType{
    FROM_NETWORK,
    FROM_LOCALDB
}

sealed class ResponseModel<T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : ResponseModel<T>(data)

    class Error<T>(message: String?, data: T? = null) : ResponseModel<T>(data, message)

    class Loading<T> : ResponseModel<T>()

    class Idle<T>(message: String) : ResponseModel<T>(null, message)

}

const val DATABASE_NAME = "sunflower-db"
const val GET_DATE_PATTERN1 = "dd-MMM-yyyy HH:mm:ss a"
const val GET_DATE_PATTERN2 = "ddMMyyyy_HHmm"

