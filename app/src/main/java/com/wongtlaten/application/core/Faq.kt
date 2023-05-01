package com.wongtlaten.application.core

// Untuk menampung identitas dari data OTP users
data class Faq(
    val idFaq : String,
    val jawaban : String,
    val pertanyaan : String
){
    constructor(): this("","", ""){

    }
}