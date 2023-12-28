package com.example.projectr.data

import androidx.compose.material3.contentColorFor

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentOrNull(): T?{
        return if(hasBeenHandled)
            null
        else{
            hasBeenHandled = true
            content
        }
    }
}