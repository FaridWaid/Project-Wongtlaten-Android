package com.wongtlaten.application.core

data class Chat(
    val idChat: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val statusMessage: String = ""
){
    constructor(): this("","","", "", ""){

    }
}