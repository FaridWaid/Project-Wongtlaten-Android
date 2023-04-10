package com.wongtlaten.application

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.CekOngkirActivity.Companion.EXTRA_ONGKIR
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.TempCostOngkir
import com.wongtlaten.application.model.City
import com.wongtlaten.application.model.Cost
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class PrediksiOngkirActivity : AppCompatActivity() {

    private lateinit var btnCekOngkir : Button
    private lateinit var prediksiOngkir : TextView
    private lateinit var daftarCityList: ArrayList<String>
    private lateinit var daftarCostList: ArrayList<TempCostOngkir>
    private lateinit var tempArray: ArrayList<TempCostOngkir>
    private lateinit var autoCompleteKotaTujuan: AutoCompleteTextView
    private lateinit var autoCompleteKotaAsal: AutoCompleteTextView
    private lateinit var beratInput : String
    private lateinit var etBerat: EditText
    private lateinit var originDetails : String
    private lateinit var destinationDetails : String
    private lateinit var codeCourier : String
    private lateinit var nameCourier : String
    private lateinit var serviceCourier : String
    private lateinit var descriptionCourier : String
    private var costCourier by Delegates.notNull<Long>()
    private lateinit var estimateCourier : String
    private lateinit var dropDownKotaAsal : String
    private lateinit var dropDownKotaTujuan : String
    private var idOrigin by Delegates.notNull<Int>()
    private var idDestination by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediksi_ongkir)

        prediksiOngkir = findViewById(R.id.textPrediksiOngkir)
        autoCompleteKotaTujuan = findViewById(R.id.autoCompleteTextViewKotaTujuan)
        autoCompleteKotaAsal = findViewById(R.id.autoCompleteTextViewKotaAsal)
        etBerat = findViewById(R.id.etBerat)
        beratInput = ""
        daftarCityList = arrayListOf<String>()
        daftarCostList = arrayListOf<TempCostOngkir>()
        tempArray = arrayListOf<TempCostOngkir>()
        originDetails = ""
        destinationDetails = ""
        codeCourier = ""
        nameCourier = ""
        serviceCourier = ""
        descriptionCourier = ""
        costCourier = 0
        estimateCourier = ""
        idDestination = 0
        idOrigin = 0

        showDataCity()

        val arrayAdapterCity = android.widget.ArrayAdapter(this, R.layout.dropdown_item, daftarCityList)
        autoCompleteKotaTujuan.setAdapter(arrayAdapterCity)
        autoCompleteKotaAsal.setAdapter(arrayAdapterCity)

        btnCekOngkir = findViewById(R.id.btnCekOngkir)

        btnCekOngkir.setOnClickListener {

            beratInput = etBerat.text.toString().trim()
            dropDownKotaAsal = autoCompleteKotaAsal.text.toString().trim()
            dropDownKotaTujuan = autoCompleteKotaTujuan.text.toString().trim()

            // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
            if (beratInput.isEmpty()){
                etBerat.error = "Masukkan berat produk terlebih dahulu!"
                etBerat.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }
            if (beratInput == "0"){
                etBerat.error = "Masukkan berat produk terlebih dahulu!"
                etBerat.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }

            // Jika dropDownJenisInput kosong maka akan muncul error harus isi terlebih dahulu
            if (dropDownKotaAsal == "Pilih Kota"){
                autoCompleteKotaAsal.error = "Silakan pilih kota asal terlebih dahulu!"
                autoCompleteKotaAsal.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }

            // Jika dropDownJenisInput kosong maka akan muncul error harus isi terlebih dahulu
            if (dropDownKotaTujuan == "Pilih Kota"){
                autoCompleteKotaTujuan.error = "Silakan pilih kota tujuan terlebih dahulu!"
                autoCompleteKotaTujuan.requestFocus()
//                checkClick = true
                return@setOnClickListener
            }

            checkIdCity(dropDownKotaAsal, dropDownKotaTujuan)
            showCostJne(idOrigin.toString(), idDestination.toString(), beratInput.toInt(), "jne")
            showCostPos(idOrigin.toString(), idDestination.toString(), beratInput.toInt(), "pos")
            tempArray = showCostTiki(idOrigin.toString(), idDestination.toString(), beratInput.toInt(), "tiki")
            if (!tempArray.isEmpty()){
                Intent(applicationContext, CekOngkirActivity::class.java).also {
                    it.putExtra(EXTRA_ONGKIR, tempArray)
                    startActivity(it)
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    finish()
                }
            } else {
                Toast.makeText(this, "Silakan klik button sekali lagi untuk mendapatkan data ongkir", Toast.LENGTH_SHORT).show()
            }
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


    private fun checkIdCity(dropDownKotaAsal: String, dropDownKotaTujuan: String) {
        for (i in 0..daftarCityList.size - 1){
            if (daftarCityList[i] == dropDownKotaAsal){
                idOrigin = i + 1
            }
            if (daftarCityList[i] == dropDownKotaTujuan){
                idDestination = i + 1
            }
        }
    }

    private fun showCostTiki(origin: String, destination: String, weight: Int, courier: String) : ArrayList<TempCostOngkir> {
        RetrofitClient.instance.getCost(origin, destination, weight, courier).enqueue(object: Callback<Cost>{
            override fun onResponse(call: Call<Cost>, response: Response<Cost>) {
                val originDetails = response.body()!!.rajaongkir.origin_details.city_name
                val destinationDetails = response.body()!!.rajaongkir.destination_details.city_name
                val codeCourier = response.body()!!.rajaongkir.results[0].code
                val nameCourier = response.body()!!.rajaongkir.results[0].name
                val resultCost = response.body()!!.rajaongkir.results[0].costs
                val countSize = resultCost.size - 1
                for (i in 0..countSize){
                    var serviceCourier = resultCost[i].service
                    var descriptionCourier = resultCost[i].description
                    var costCourier = resultCost[i].cost[0].value
                    var estimateCourier = resultCost[i].cost[0].etd
                    var newCost = TempCostOngkir(originDetails, destinationDetails, weight, codeCourier, nameCourier, serviceCourier, descriptionCourier, costCourier.toLong(), estimateCourier)
                    daftarCostList.add(newCost)
                }
            }

            override fun onFailure(call: Call<Cost>, t: Throwable) {
                Log.d("cost", "${t.message}")
            }

        })
        return daftarCostList
    }

    private fun showCostPos(origin: String, destination: String, weight: Int, courier: String) {
        RetrofitClient.instance.getCost(origin, destination, weight, courier).enqueue(object: Callback<Cost>{
            override fun onResponse(call: Call<Cost>, response: Response<Cost>) {
                val originDetails = response.body()!!.rajaongkir.origin_details.city_name
                val destinationDetails = response.body()!!.rajaongkir.destination_details.city_name
                val codeCourier = response.body()!!.rajaongkir.results[0].code
                val nameCourier = response.body()!!.rajaongkir.results[0].name
                val resultCost = response.body()!!.rajaongkir.results[0].costs
                val countSize = resultCost.size - 1
                for (i in 0..countSize){
                    var serviceCourier = resultCost[i].service
                    var descriptionCourier = resultCost[i].description
                    var costCourier = resultCost[i].cost[0].value
                    var estimateCourier = resultCost[i].cost[0].etd
                    var newCost = TempCostOngkir(originDetails, destinationDetails, weight, codeCourier, nameCourier, serviceCourier, descriptionCourier, costCourier.toLong(), estimateCourier)
                    daftarCostList.add(newCost)
                }
            }

            override fun onFailure(call: Call<Cost>, t: Throwable) {
                Log.d("cost", "${t.message}")
            }

        })
    }

    private fun showCostJne(origin: String, destination: String, weight: Int, courier: String) {
        RetrofitClient.instance.getCost(origin, destination, weight, courier).enqueue(object: Callback<Cost>{
            override fun onResponse(call: Call<Cost>, response: Response<Cost>) {
                val originDetails = response.body()!!.rajaongkir.origin_details.city_name
                val destinationDetails = response.body()!!.rajaongkir.destination_details.city_name
                val codeCourier = response.body()!!.rajaongkir.results[0].code
                val nameCourier = response.body()!!.rajaongkir.results[0].name
                val resultCost = response.body()!!.rajaongkir.results[0].costs
                val countSize = resultCost.size - 1
                for (i in 0..countSize){
                    var serviceCourier = resultCost[i].service
                    var descriptionCourier = resultCost[i].description
                    var costCourier = resultCost[i].cost[0].value
                    var estimateCourier = resultCost[i].cost[0].etd
                    var newCost = TempCostOngkir(originDetails, destinationDetails, weight, codeCourier, nameCourier, serviceCourier, descriptionCourier, costCourier.toLong(), estimateCourier)
                    daftarCostList.add(newCost)
                }
            }

            override fun onFailure(call: Call<Cost>, t: Throwable) {
                Log.d("cost", "${t.message}")
            }

        })
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
                Toast.makeText(this@PrediksiOngkirActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }


        })
    }

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
    private fun alertDialog(title: String, message: String, backActivity: Boolean){
        // Membuat variabel yang berisikan AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle(title)
            setMessage(message)
            window.setBackgroundDrawableResource(android.R.color.background_light)
            setCancelable(false)
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (backActivity){
                        onBackPressed()
                    }
                })
        }
        alertDialog.show()
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}