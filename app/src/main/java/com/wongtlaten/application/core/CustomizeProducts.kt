package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class CustomizeProducts(
    val idProduct : String,
    val namaProduct : String,
    val hargaProduct : Long,
    val stockProduct : Int,
    val beratProduct : Int,
    val panjangProduct : Float,
    val lebarProduct : Float,
    val kategoriProduct : String,
    val deskripsiProduct : String,
    val photoProduct1 : String,
    val statusProduct : String
): java.io.Serializable{
    constructor(): this("","", 0, 0, 0, 0.0F, 0.0F,  "", "", "", ""){

    }
}