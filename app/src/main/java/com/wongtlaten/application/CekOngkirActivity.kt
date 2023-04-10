package com.wongtlaten.application

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.TempCostOngkir
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualAdapter

class CekOngkirActivity : AppCompatActivity() {

    private lateinit var rvDaftarOngkir: RecyclerView
    private lateinit var adapter: PrediksiOngkirAdapter
    private lateinit var daftarOngkir : ArrayList<TempCostOngkir>
    private lateinit var textKotaAsal : TextView
    private lateinit var textKotaTujuan : TextView
    private lateinit var textBerat : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_ongkir)

        daftarOngkir = intent.getSerializableExtra(EXTRA_ONGKIR) as ArrayList<TempCostOngkir>
        textKotaAsal = findViewById(R.id.textKotaAsal)
        textKotaTujuan = findViewById(R.id.textKotaTujuan)
        textBerat = findViewById(R.id.textBerat)

        textKotaAsal.text = daftarOngkir[0].origin
        textKotaTujuan.text = daftarOngkir[0].destination
        textBerat.text = "(${daftarOngkir[0].weight} KG)"

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarOngkir = findViewById(R.id.rvDaftarOngkir)
        rvDaftarOngkir.setHasFixedSize(true)
        rvDaftarOngkir.layoutManager = LinearLayoutManager(this)

        adapter = PrediksiOngkirAdapter(daftarOngkir)
        rvDaftarOngkir.adapter = adapter

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val EXTRA_ONGKIR = "EXTRA_ONGKIR"
    }

}