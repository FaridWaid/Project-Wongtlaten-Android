package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class Products(
    val idProduct : String,
    val namaProduct : String,
    val hargaProduct : Long,
    val stockProduct : Int,
    val minimumPemesananProduct : Int,
    val beratProduct : Int,
    val kategoriProduct : String,
    val deskripsiProduct : String,
    val jenisProduct : String,
    val hargaPromoProduct : Long,
    val photoProduct1 : String,
    val photoProduct2 : String,
    val photoProduct3 : String,
    val photoProduct4 : String,
    val ratingProduct : Float,
    val jumlahPembelianProduct : Int
){
    constructor(): this("","", 0, 0, 0, 0, "", "", "", 0, "", "", "", "", 0F, 0){

    }
}