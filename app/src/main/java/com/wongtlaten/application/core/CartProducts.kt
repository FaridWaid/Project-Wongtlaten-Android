package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class CartProducts(
    val idUser : String,
    val idProduct : String
){
    constructor(): this("",""){

    }
}