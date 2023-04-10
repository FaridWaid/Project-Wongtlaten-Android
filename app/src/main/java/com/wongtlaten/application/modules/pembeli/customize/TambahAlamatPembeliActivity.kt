package com.wongtlaten.application.modules.pembeli.customize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.model.City
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_ALAMAT_PENERIMA
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_KOTA_PENERIMA
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_NAMA_PENERIMA
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_POS_PENERIMA
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_TELEPON_PENERIMA
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahAlamatPembeliActivity : AppCompatActivity() {

    private lateinit var etNamaPenerima: EditText
    private lateinit var daftarCityList: ArrayList<String>
    private lateinit var autoCompleteKota: AutoCompleteTextView
    private lateinit var etAlamatLengkap: EditText
    private lateinit var etKodePos: EditText
    private lateinit var etTelepon: EditText
    private lateinit var btnSimpan: Button
    private lateinit var namaPenerima: String
    private lateinit var alamatLengkap: String
    private lateinit var kodePos: String
    private lateinit var teleponPenerima: String
    private lateinit var dropDownKota: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_alamat_pembeli)

        etNamaPenerima = findViewById(R.id.etNamaPenerima)
        autoCompleteKota = findViewById(R.id.autoCompleteTextViewKota)
        etAlamatLengkap = findViewById(R.id.etAlamatLengkap)
        etKodePos = findViewById(R.id.etKodePos)
        etTelepon = findViewById(R.id.etTelepon)
        btnSimpan = findViewById(R.id.btnSimpan)
        daftarCityList = arrayListOf<String>()

        showDataCity()

        val arrayAdapterCity = android.widget.ArrayAdapter(this, R.layout.dropdown_item, daftarCityList)
        autoCompleteKota.setAdapter(arrayAdapterCity)

        btnSimpan.setOnClickListener {

            namaPenerima = etNamaPenerima.text.toString().trim()
            alamatLengkap = etAlamatLengkap.text.toString().trim()
            kodePos = etKodePos.text.toString().trim()
            teleponPenerima = etTelepon.text.toString().trim()
            dropDownKota = autoCompleteKota.text.toString().trim()

            // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
            if (namaPenerima.isEmpty()){
                etNamaPenerima.error = "Masukkan nama penerima terlebih dahulu!"
                etNamaPenerima.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (alamatLengkap.isEmpty()){
                etAlamatLengkap.error = "Masukkan alamat lengkap penerima terlebih dahulu!"
                etAlamatLengkap.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (kodePos.isEmpty()){
                etKodePos.error = "Masukkan kode pos penerima terlebih dahulu!"
                etKodePos.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (kodePos.length != 5){
                etKodePos.error = "Masukkan kode pos yang valid!"
                etKodePos.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (teleponPenerima.isEmpty()){
                etTelepon.error = "Masukkan nomor telepon penerima terlebih dahulu!"
                etTelepon.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (teleponPenerima.substring(0,2) != "08"){
                etTelepon.error = "Masukkan nomor telepon yang valid!"
                etTelepon.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if(teleponPenerima.length < 10) {
                etTelepon.error = "Masukkan nomor telepon yang valid!"
                etTelepon.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if(teleponPenerima.length > 13) {
                etTelepon.error = "Masukkan nomor telepon yang valid!"
                etTelepon.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (dropDownKota == "Pilih Kota"){
                autoCompleteKota.error = "Silakan pilih kota terlebih dahulu!"
                autoCompleteKota.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }

            var intent = Intent()
            intent.putExtra(EXTRA_NAMA_PENERIMA, namaPenerima)
            intent.putExtra(EXTRA_KOTA_PENERIMA, dropDownKota)
            intent.putExtra(EXTRA_POS_PENERIMA, kodePos)
            intent.putExtra(EXTRA_ALAMAT_PENERIMA, alamatLengkap)
            intent.putExtra(EXTRA_TELEPON_PENERIMA, teleponPenerima)
            setResult(RESULT_OK, intent);
            finish()
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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

    fun showDataCity(){
        RetrofitClient.instance.getCity().enqueue(object : Callback<City> {
            override fun onResponse(call: Call<City>, response: Response<City>) {
                daftarCityList.clear()
                val resultProvince = response.body()!!.rajaongkir.results
                val countSize = resultProvince.size - 1
                for (i in 0..countSize){
                    daftarCityList.add(resultProvince[i].city_name)
                }
            }

            override fun onFailure(call: Call<City>, t: Throwable) {
//                Toast.makeText(this@PrediksiOngkirActivity, "Gagal mengambil data kota", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@TambahAlamatPembeliActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }


        })
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}