package com.wongtlaten.application.modules.pembeli.customize

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.*
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.model.City
import com.wongtlaten.application.model.Cost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap
import kotlin.properties.Delegates

class DetailCustomizeProdukPembeliActivity : AppCompatActivity() {

    private lateinit var referenceTransaksi: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userIdentity: FirebaseUser
    private lateinit var daftarProduk : ArrayList<CustomizeProducts>
    private lateinit var daftarCityList : ArrayList<String>
    private lateinit var daftarIdProduk : HashMap<String, Int>
    private lateinit var itemDetails: ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var adapterDetailCustomize: DetailCustomizePembeliAdapter
    private lateinit var btnUbahJasaPengiriman : Button
    private lateinit var btnTambahAlamat : Button
    private lateinit var btnBayarSekarang : Button
    private lateinit var textTotalHarga : TextView
    private lateinit var totalKotakGiftbox : TextView
    private lateinit var totalHarga : TextView
    private lateinit var ongkosKirim : TextView
    private lateinit var totalTransaksi : TextView
    private lateinit var textPengiriman : TextView
    private lateinit var namaPenerima : TextView
    private lateinit var kotaPenerima : TextView
    private lateinit var alamatPenerima : TextView
    private lateinit var teleponPenerima : TextView
    private lateinit var etPesanGiftcard: TextInputEditText
    private lateinit var pesanContainer: TextInputLayout
    private lateinit var textJasaPengiriman : String
    private lateinit var textNamaPenerima : String
    private lateinit var textKotaPenerima : String
    private lateinit var textPosPenerima : String
    private lateinit var textTeleponPenerima : String
    private lateinit var textAlamatPenerima : String
    private lateinit var idTransaksi: String
    private lateinit var pesanGiftcardInput: String
    private lateinit var namaUser: String
    private lateinit var alamatUser: String
    private lateinit var teleponUser: String
    private lateinit var pdfUrl: String
    private var totalHargaGiftbox by Delegates.notNull<Long>()
    private var totalHargaProduk by Delegates.notNull<Long>()
    private var countWeight by Delegates.notNull<Int>()
    private var idOrigin by Delegates.notNull<Int>()
    private var idDestination by Delegates.notNull<Int>()
    private var costCourier by Delegates.notNull<Long>()
    private var totalPembayaran by Delegates.notNull<Long>()
    private var hargaSatuanProduk by Delegates.notNull<Long>()
    private lateinit var tempProductTransaction: ArrayList<ProductTransaction>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_customize_produk_pembeli)

        referenceTransaksi = FirebaseDatabase.getInstance().getReference("dataTransaksi")

        auth = FirebaseAuth.getInstance()
        userIdentity = auth.currentUser!!

        totalHargaGiftbox = intent.getLongExtra(EXTRA_HARGA_GIFTBOX, 0)
        daftarProduk = intent.getSerializableExtra(EXTRA_PRODUK) as ArrayList<CustomizeProducts>
        daftarIdProduk = intent.getSerializableExtra(EXTRA_ID_PRODUK) as HashMap<String, Int>
        daftarCityList = arrayListOf()
        tempProductTransaction = arrayListOf()
        itemDetails = arrayListOf()

        totalKotakGiftbox = findViewById(R.id.kotakGiftbox)
        btnUbahJasaPengiriman = findViewById(R.id.btnUbahJasaPengiriman)
        textPengiriman = findViewById(R.id.pengiriman)
        btnTambahAlamat = findViewById(R.id.btnTambahAlamat)
        btnBayarSekarang = findViewById(R.id.btnBayarSekarang)
        textTotalHarga = findViewById(R.id.textTotalKustomisasi)
        totalHarga = findViewById(R.id.totalKustomisasi)
        ongkosKirim = findViewById(R.id.ongkosKirim)
        totalTransaksi = findViewById(R.id.total)
        namaPenerima = findViewById(R.id.namaPenerima)
        kotaPenerima = findViewById(R.id.kota)
        alamatPenerima = findViewById(R.id.alamatLengkap)
        teleponPenerima = findViewById(R.id.nomorTelepon)
        etPesanGiftcard = findViewById(R.id.etPesanGiftcard)
        pesanContainer = findViewById(R.id.pesanContainer)

        textJasaPengiriman = "jne"
        textNamaPenerima = ""
        textKotaPenerima = ""
        textPosPenerima = ""
        textTeleponPenerima = ""
        textAlamatPenerima = ""
        idTransaksi = ""
        pesanGiftcardInput = ""
        namaUser = ""
        alamatUser = ""
        teleponUser = ""
        totalHargaProduk = 0
        countWeight = 0
        idDestination = 0
        idOrigin = 0
        costCourier = 0
        totalPembayaran = 0
        hargaSatuanProduk = 0
        pdfUrl = ""

        val formatter: NumberFormat = DecimalFormat("#,###")
        val formattedNumberPrice: String = formatter.format(totalHargaGiftbox)
        totalKotakGiftbox.text = "Rp. $formattedNumberPrice"

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = LinearLayoutManager(this)

        adapterDetailCustomize = DetailCustomizePembeliAdapter(daftarProduk, daftarIdProduk)
        adapterDetailCustomize.setOnItemClickCallback(object : DetailCustomizePembeliAdapter.OnItemClickCallback{
            override fun onItemClicked(countProduct: Int, totalWeight: Int, countTotal: Long) {
                totalHargaProduk = countTotal
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(countTotal)
                textTotalHarga.text = "Total harga (${countProduct} produk)"
                totalHarga.text = "Rp. ${formattedNumberPrice}"
                countWeight = totalWeight
            }

        })
        rvDaftarProduk.adapter = adapterDetailCustomize

        showDataCity()

        btnUbahJasaPengiriman.setOnClickListener {
            val i = Intent(this, UbahJasaPengirimanPembeliActivity::class.java)
            startActivityForResult(i, 1)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        btnTambahAlamat.setOnClickListener {
            val i = Intent(this, TambahAlamatPembeliActivity::class.java)
            startActivityForResult(i, 2)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        pesanGiftcardFocusListener()

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-W_fr1Eg_qXdR7Ssz") // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback(object : TransactionFinishedCallback {
                override fun onTransactionFinished(result: TransactionResult) {
                    if (result.response != null){
                        if (result.response.pdfUrl != null){
                            pdfUrl = result.response.pdfUrl
                        }
                        if (result.status == TransactionResult.STATUS_SUCCESS){
                            val newTransaction = Transaction(userIdentity.uid, idTransaksi, "custom", textNamaPenerima, textKotaPenerima, textPosPenerima, textAlamatPenerima, textTeleponPenerima, countWeight, costCourier, result.response.grossAmount.toDouble(), result.response.paymentType, result.response.transactionTime, "", result.response.transactionStatus, "Belum Diproses", textJasaPengiriman, "", pesanGiftcardInput, pdfUrl, tempProductTransaction)
                            referenceTransaksi.child(idTransaksi).setValue(newTransaction).addOnCompleteListener {
                                if (it.isSuccessful){
                                    updateStockProduct(daftarIdProduk, daftarProduk)
                                    Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi berhasil", Toast.LENGTH_SHORT).show()
                                    Intent(applicationContext, PembelianBerhasilActivity::class.java).also {
                                        startActivity(it)
                                        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
                                        finish()
                                    }
                                } else{
                                    Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi gagal ditambahkan di database", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else if (result.status == TransactionResult.STATUS_PENDING){
                            val newTransaction = Transaction(userIdentity.uid, idTransaksi, "custom", textNamaPenerima, textKotaPenerima, textPosPenerima, textAlamatPenerima, textTeleponPenerima, countWeight, costCourier, result.response.grossAmount.toDouble(), result.response.paymentType, result.response.transactionTime, "", result.response.transactionStatus, "Belum Diproses", textJasaPengiriman, "", pesanGiftcardInput, pdfUrl, tempProductTransaction)
                            referenceTransaksi.child(idTransaksi).setValue(newTransaction).addOnCompleteListener {
                                if (it.isSuccessful){
                                    updateStockProduct(daftarIdProduk, daftarProduk)
                                    Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi pending", Toast.LENGTH_SHORT).show()
                                    Intent(applicationContext, PembelianBerhasilActivity::class.java).also {
                                        startActivity(it)
                                        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
                                        finish()
                                    }
                                } else{
                                    Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi gagal ditambahkan di database", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else if (result.status == TransactionResult.STATUS_FAILED){
                            Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi gagal", Toast.LENGTH_SHORT).show()
                        }
                    } else if (result.isTransactionCanceled){
                        Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi canceled", Toast.LENGTH_SHORT).show()
                    } else{
                        if (result.status == TransactionResult.STATUS_INVALID){
                            Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi invalid", Toast.LENGTH_SHORT).show()
                        } else{
                            Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "Transaksi finished with failure", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }) // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("https://wongtlaten-midtrans.000webhostapp.com/index.php/") //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .setLanguage("id") //`en` for English and `id` for Bahasa
            .buildSDK()

        btnBayarSekarang.setOnClickListener {
            pesanGiftcardInput = etPesanGiftcard.text.toString().trim()
            pesanContainer.helperText = validPesan()

            val validPesanGiftcard = pesanContainer.helperText == null

            if (validPesanGiftcard){
                if (textNamaPenerima == ""){
                    Toast.makeText(this, "Tambah alamat terlebih dahulu", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                //TransactionRequest on revamp is included in the startPayment Constructor
                itemDetails.clear()
                tempProductTransaction.clear()
                val formatter = DateTimeFormatter.ofPattern("yyMMddHHmm")
                val current = LocalDateTime.now().format(formatter)
                idTransaksi = "${userIdentity.uid}-"+current.toString()
                val transactionRequest = TransactionRequest(idTransaksi, totalPembayaran.toDouble())

                for (i in 0..daftarIdProduk.size-1){
                    hargaSatuanProduk = daftarProduk[i].hargaProduct
                    val newTempTransaction = ProductTransaction(daftarProduk[i].idProduct, hargaSatuanProduk.toDouble(), daftarProduk[i].beratProduct, daftarIdProduk.getValue(daftarProduk[i].idProduct))
                    tempProductTransaction.add(newTempTransaction)
                    val detail = com.midtrans.sdk.corekit.models.ItemDetails(daftarProduk[i].idProduct, hargaSatuanProduk.toDouble(), daftarIdProduk.getValue(daftarProduk[i].idProduct), daftarProduk[i].namaProduct)
                    itemDetails.add(detail)
                }
                val detailOngkir = com.midtrans.sdk.corekit.models.ItemDetails("ongkir-"+userIdentity.uid+System.currentTimeMillis().toString(), costCourier.toDouble(), 1, "Ongkir "+textJasaPengiriman)
                itemDetails.add(detailOngkir)
                val detailKotakGiftbox = com.midtrans.sdk.corekit.models.ItemDetails("giftbox-"+userIdentity.uid+System.currentTimeMillis().toString(), totalHargaGiftbox.toDouble(), 1, "Kotak Giftbox")
                itemDetails.add(detailKotakGiftbox)
                uiKitDetails(transactionRequest, userIdentity.uid)
                transactionRequest.itemDetails = itemDetails
                MidtransSDK.getInstance().transactionRequest = transactionRequest
                MidtransSDK.getInstance().startPaymentUiFlow(this)
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

    private fun updateStockProduct(daftarIdProduk: HashMap<String, Int>, daftarProduk: kotlin.collections.ArrayList<CustomizeProducts>){
        for (i in 0..daftarIdProduk.size-1){
            var updateProduct = CustomizeProducts(daftarProduk[i].idProduct, daftarProduk[i].namaProduct, daftarProduk[i].hargaProduct, daftarProduk[i].stockProduct - daftarIdProduk.getValue(daftarProduk[i].idProduct), daftarProduk[i].beratProduct, daftarProduk[i].kategoriProduct, daftarProduk[i].deskripsiProduct, daftarProduk[i].photoProduct1)
            val reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(daftarProduk[i].idProduct)
            reference.setValue(updateProduct)
        }
    }

    fun uiKitDetails(transactionRequest: TransactionRequest, idUser: String){
        val referen = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(idUser)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                alamatUser = users.alamat
                namaUser = users.username
                teleponUser = users.noTelp
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = textNamaPenerima
        customerDetails.phone = textTeleponPenerima
        customerDetails.firstName = textNamaPenerima
        val shippingAddress = ShippingAddress()
        shippingAddress.address = textAlamatPenerima
        shippingAddress.city = textKotaPenerima
        shippingAddress.postalCode = textPosPenerima
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address  = alamatUser
        billingAddress.firstName = namaUser
        billingAddress.phone = teleponUser
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
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
                Toast.makeText(this@DetailCustomizeProdukPembeliActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun showCost(origin: String, destination: String, weight: Int, courier: String){
        RetrofitClient.instance.getCost(origin, destination, weight, courier).enqueue(object:
            Callback<Cost> {
            override fun onResponse(call: Call<Cost>, response: Response<Cost>) {
                costCourier = response.body()!!.rajaongkir.results[0].costs[0].cost[0].value.toLong()
                totalPembayaran = costCourier + totalHargaProduk + totalHargaGiftbox
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(costCourier)
                val formattedNumberPrice2: String = formatter.format(totalPembayaran)
                ongkosKirim.text = "Rp. $formattedNumberPrice"
                totalTransaksi.text = "Rp. $formattedNumberPrice2"
            }

            override fun onFailure(call: Call<Cost>, t: Throwable) {
                Log.d("cost", "${t.message}")
            }

        })
    }

    // Memanggi fungsi turunan "onActivityResult", fungsi ini berjalan ketika activity dibuka
    // Fungsi ini digunakan untuk mengambil image yang telah dipilih di gallery dan dipasang ke photoProduct,
    // dan dimasukkan ke dalam database img, dengan id dari user
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {
                textJasaPengiriman = data?.getStringExtra(EXTRA_JASA_PENGIRIMAN)!!
                textPengiriman.setText(textJasaPengiriman)
            }
        } else if (requestCode === 2) {
            if (resultCode === RESULT_OK) {
                textNamaPenerima = data?.getStringExtra(EXTRA_NAMA_PENERIMA)!!
                textKotaPenerima = data?.getStringExtra(EXTRA_KOTA_PENERIMA)!!
                textPosPenerima = data?.getStringExtra(EXTRA_POS_PENERIMA)!!
                textTeleponPenerima = data?.getStringExtra(EXTRA_TELEPON_PENERIMA)!!
                textAlamatPenerima = data?.getStringExtra(EXTRA_ALAMAT_PENERIMA)!!
                namaPenerima.text = textNamaPenerima
                kotaPenerima.text = "$textKotaPenerima/$textPosPenerima"
                teleponPenerima.text = textTeleponPenerima
                alamatPenerima.text = textAlamatPenerima
                for (i in 0..daftarCityList.size - 1){
                    if (daftarCityList[i] == "Sidoarjo"){
                        idOrigin = i + 1
                    }
                    if (daftarCityList[i] == textKotaPenerima){
                        idDestination = i + 1
                    }
                }
            }
            if (textKotaPenerima.isNotEmpty()){
                showCost("$idOrigin", "$idDestination", countWeight, textJasaPengiriman.toLowerCase())
            }
        }
    }

    // Membuat fungsi "namaProdukFocusListener"
    private fun pesanGiftcardFocusListener() {
        // Memastikan apakah etNamaProduk sudah sesuai dengan format pengisian
        etPesanGiftcard.setOnFocusChangeListener { _, focused ->
            if(!focused){
                pesanContainer.helperText = validPesan()
            }
        }
    }

    // Membuat fungsi "validNamaProduk"
    private fun validPesan(): String? {
        val pesanGiftcard = etPesanGiftcard.text.toString()
        // Jika namaProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (pesanGiftcard.isEmpty()){
            return "Pesan pada giftcard Harus Diisi!"
        }
        if (pesanGiftcard.length > 100){
            return "Pesan pada giftcard terlalu panjang (maksimal 100 karakter)!"
        }
        return null
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val EXTRA_PRODUK = "EXTRA_PRODUK"
        const val EXTRA_ID_PRODUK = "EXTRA_ID_PRODUK"
        const val EXTRA_HARGA_GIFTBOX = "EXTRA_HARGA_GIFTBOX"
        const val EXTRA_JASA_PENGIRIMAN = "EXTRA_JASA_PENGIRIMAN"
        const val EXTRA_NAMA_PENERIMA = "EXTRA_NAMA_PENERIMA"
        const val EXTRA_KOTA_PENERIMA = "EXTRA_KOTA_PENERIMA"
        const val EXTRA_POS_PENERIMA = "EXTRA_POS_PENERIMA"
        const val EXTRA_ALAMAT_PENERIMA = "EXTRA_ALAMAT_PENERIMA"
        const val EXTRA_TELEPON_PENERIMA = "EXTRA_TELEPON_PENERIMA"
    }

}