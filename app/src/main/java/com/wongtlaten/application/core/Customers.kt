package com.wongtlaten.application.core

// Untuk menampung identitas dari data users
data class Customers(
    val idCustomers : String,
    val username : String,
    val kelamin : String,
    val alamat : String,
    val email : String,
    val photoProfil : String,
    val noTelp : String,
    val jumlahTransaksi : Int,
    val accessLevel : String,
    val token : String,
    val status : String,
    val checkOtp : String
){
    constructor(): this("","", "", "", "", "", "", 0, "", "", "", ""){

    }
}