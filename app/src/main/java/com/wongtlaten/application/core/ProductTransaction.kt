package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class ProductTransaction(
    var idProduk: String,
    var hargaProduk: Double,
    var beratProduk: Int,
    var totalBeli: Int,
    var statusReview: String
) : java.io.Serializable {
    constructor(): this("", 0.0, 0, 0, ""){
    }
}