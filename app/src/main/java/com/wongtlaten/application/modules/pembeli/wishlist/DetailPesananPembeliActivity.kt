package com.wongtlaten.application.modules.pembeli.wishlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Transaction
import java.text.DecimalFormat
import java.text.NumberFormat


class DetailPesananPembeliActivity : AppCompatActivity() {

    private lateinit var daftarTransaction: ArrayList<Transaction>
    private lateinit var textStatusPesanan: TextView
    private lateinit var idTransaksi: TextView
    private lateinit var tanggalPemesanan: TextView
    private lateinit var tanggalPengiriman: TextView
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var adapter: DetailPesananPembeliAdapter
    private lateinit var kurir: TextView
    private lateinit var noResi: TextView
    private lateinit var namaPenerima: TextView
    private lateinit var nomorPenerima: TextView
    private lateinit var alamatPenerima: TextView
    private lateinit var noteGiftcard: TextView
    private lateinit var metodePembayaran: TextView
    private lateinit var textTotalHarga: TextView
    private lateinit var totalHarga: TextView
    private lateinit var textOngkosKirim: TextView
    private lateinit var ongkosKirim: TextView
    private lateinit var totalPembayaran: TextView
    private lateinit var btnCaraPembayaran: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan_pembeli)

        daftarTransaction = intent.getSerializableExtra(EXTRA_TRANSACTION) as ArrayList<Transaction>

        textStatusPesanan = findViewById(R.id.textStatusPesanan)
        idTransaksi = findViewById(R.id.idTransaksi)
        tanggalPemesanan = findViewById(R.id.tanggalPemesanan)
        tanggalPengiriman = findViewById(R.id.tanggalPengiriman)
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        kurir = findViewById(R.id.kurir)
        noResi = findViewById(R.id.noResi)
        namaPenerima = findViewById(R.id.namaPenerima)
        nomorPenerima = findViewById(R.id.nomorPenerima)
        alamatPenerima = findViewById(R.id.AlamatPenerima)
        noteGiftcard = findViewById(R.id.noteGiftcard)
        metodePembayaran = findViewById(R.id.metodePembayaran)
        textTotalHarga = findViewById(R.id.textTotalHarga)
        totalHarga = findViewById(R.id.totalHarga)
        textOngkosKirim = findViewById(R.id.textOngkosKirim)
        ongkosKirim = findViewById(R.id.totalOngkir)
        totalPembayaran = findViewById(R.id.totalPembayaran)
        btnCaraPembayaran = findViewById(R.id.btnCaraPembayaran)

        getData()

        btnCaraPembayaran.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(daftarTransaction[0].pdfUrl))
            startActivity(browserIntent)
        }

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun getData(){
        if (daftarTransaction[0].statusPembayaran == "pending"){
            textStatusPesanan.text = "Belum dibayar"
        } else if (daftarTransaction[0].statusPembayaran == "settlement"){
            textStatusPesanan.text = "Lunas"
        } else{
            textStatusPesanan.text = "Transaksi Batal"
        }
        idTransaksi.text = daftarTransaction[0].idTransaksi
        tanggalPemesanan.text = daftarTransaction[0].waktuTransaksi
        if (daftarTransaction[0].waktuPengiriman == ""){
            tanggalPengiriman.text = "Barang belum dikirimkan"
        } else{
            tanggalPengiriman.text = daftarTransaction[0].waktuPengiriman
        }
        kurir.text = daftarTransaction[0].kurir.toUpperCase()
        if (daftarTransaction[0].resiPengiriman == ""){
            noResi.text = "Barang belum dikirimkan"
        } else{
            noResi.text = daftarTransaction[0].resiPengiriman
        }
        namaPenerima.text = daftarTransaction[0].namePenerima
        nomorPenerima.text = daftarTransaction[0].teleponPenerima
        alamatPenerima.text = "${daftarTransaction[0].alamatLengkap} (${daftarTransaction[0].kodePos})"
        noteGiftcard.text = daftarTransaction[0].catatanGiftcard
        metodePembayaran.text = daftarTransaction[0].typePembayaran
        var count = 0
        for (i in 0..daftarTransaction[0].produkTransaction.size - 1){
            count += daftarTransaction[0].produkTransaction[i].totalBeli
        }
        textTotalHarga.text = "Total Harga ($count barang)"
        var countTotalProduk: Double = 0.0
        for (i in 0..daftarTransaction[0].produkTransaction.size - 1){
            countTotalProduk += daftarTransaction[0].produkTransaction[i].hargaProduk * daftarTransaction[0].produkTransaction[i].totalBeli
        }
        val formatter: NumberFormat = DecimalFormat("#,###")
        val formattedNumberPrice: String = formatter.format(countTotalProduk)
        totalHarga.text = "Rp. $formattedNumberPrice"
        textOngkosKirim.text = "Ongkos Kirim (${daftarTransaction[0].totalBerat} gr)"
        val formattedNumberPrice2: String = formatter.format(daftarTransaction[0].jumlahOngkir)
        ongkosKirim.text = "Rp. $formattedNumberPrice2"
        val formattedNumberPrice3: String = formatter.format(daftarTransaction[0].totalPembayaran)
        totalPembayaran.text = "Rp. $formattedNumberPrice3"

        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = LinearLayoutManager(this@DetailPesananPembeliActivity)
        adapter = DetailPesananPembeliAdapter(daftarTransaction)
        rvDaftarProduk.adapter = adapter
    }


    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val EXTRA_TRANSACTION = "EXTRA_TRANSACTION"
    }

}