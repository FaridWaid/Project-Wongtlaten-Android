package com.wongtlaten.application.modules.penjual.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.core.Transaction
import java.text.DecimalFormat
import java.text.NumberFormat

class DetailPaymentPenjualActivity : AppCompatActivity() {

    private lateinit var daftarTransaction: ArrayList<Transaction>
    private lateinit var textStatusPesanan: TextView
    private lateinit var idTransaksi: TextView
    private lateinit var idCustomer: TextView
    private lateinit var namaCustomer: TextView
    private lateinit var tanggalPemesanan: TextView
    private lateinit var tanggalPengiriman: TextView
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var adapter: DetailPesananPenjualAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_payment_penjual)

        daftarTransaction = intent.getSerializableExtra(DetailPesananPenjualActivity.EXTRA_TRANSACTION) as ArrayList<Transaction>

        textStatusPesanan = findViewById(R.id.textStatusPesanan)
        idTransaksi = findViewById(R.id.idTransaksi)
        idCustomer = findViewById(R.id.idCustomer)
        namaCustomer = findViewById(R.id.namaCustomer)
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

        getData()

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
        }
        idTransaksi.text = daftarTransaction[0].idTransaksi
        var referenceUser = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(daftarTransaction[0].idUser)
        val menuListener3 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                namaCustomer.text = users.username
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referenceUser.addListenerForSingleValueEvent(menuListener3)
        idCustomer.text = daftarTransaction[0].idUser
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
        rvDaftarProduk.layoutManager = LinearLayoutManager(this@DetailPaymentPenjualActivity)
        adapter = DetailPesananPenjualAdapter(daftarTransaction)
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