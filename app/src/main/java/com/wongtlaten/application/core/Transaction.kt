package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class Transaction(
    val idUser: String,
    val idTransaksi: String,
    val jenisTransaksi: String,
    val namePenerima: String,
    val kotaTujuan: String,
    val kodePos: String,
    val alamatLengkap: String,
    val teleponPenerima: String,
    val totalBerat: Int,
    val jumlahOngkir: Long,
    val totalPembayaran: Double,
    val typePembayaran: String,
    val waktuTransaksi: String,
    val waktuPengiriman: String,
    val statusPembayaran: String,
    val statusProduk: String,
    val kurir: String,
    val resiPengiriman: String,
    val catatanGiftcard: String,
    val pdfUrl: String,
    val produkTransaction : ArrayList<ProductTransaction>
) : java.io.Serializable {
    constructor(): this("","", "", "", "", "", "", "", 0, 0, 0.0, "","", "", "", "", "", "", "", "", arrayListOf()){

    }
}