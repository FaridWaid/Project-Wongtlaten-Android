package com.wongtlaten.application.core

// Untuk menampung identitas dari data category product
data class CategoryProduct(
    val idCategory : String,
    val namaCategory : String
){
    constructor(): this("",""){
    }
}