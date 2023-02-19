package com.wongtlaten.application.core

// Untuk menampung identitas dari data attempt login user
data class AttemptLogin(
    val email : String,
    val attempt : Int
){
    constructor(): this("", 0){

    }
}