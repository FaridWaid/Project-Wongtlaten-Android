package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class ReviewProduct(
    var idUser: String,
    var idProduk: String,
    var idReview: String,
    var rating: Float,
    var review: String
) : java.io.Serializable {
    constructor(): this("", "", "", 0.0F, ""){
    }
}