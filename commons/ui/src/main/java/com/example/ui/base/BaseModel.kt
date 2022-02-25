package com.example.ui.base


abstract class BaseModel {
    fun isItemSame(objects: BaseModel): Boolean {
        return objects.hashCode() == hashCode()
    }

    fun isContentSame(objects: BaseModel): Boolean {
        return this == objects
    }
}