package com.wongtlaten.application.core

// Untuk menampung identitas dari data OTP users
data class Otp(
    val idCustomers : String,
    val email : String,
    val otp : String
){
    constructor(): this("","", ""){

    }
}