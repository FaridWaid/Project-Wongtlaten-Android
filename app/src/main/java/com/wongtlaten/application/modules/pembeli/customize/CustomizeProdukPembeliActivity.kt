package com.wongtlaten.application.modules.pembeli.customize

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.SectionCustomize
import com.wongtlaten.application.modules.pembeli.customize.DetailCustomizeProdukPembeliActivity.Companion.EXTRA_HARGA_GIFTBOX
import com.wongtlaten.application.modules.pembeli.customize.DetailCustomizeProdukPembeliActivity.Companion.EXTRA_ID_PRODUK
import com.wongtlaten.application.modules.pembeli.customize.DetailCustomizeProdukPembeliActivity.Companion.EXTRA_PRODUK
import com.wongtlaten.application.modules.pembeli.home.NewActivity
import com.wongtlaten.application.modules.pembeli.home.NewAdapter
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates


class CustomizeProdukPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    private lateinit var totalCustomize: RelativeLayout
    private lateinit var totalProduk: TextView
    private lateinit var daftarProduk: TextView
    private lateinit var totalPrice: TextView
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var daftarProdukListKecil: ArrayList<CustomizeProducts>
    private lateinit var daftarProdukListSedang: ArrayList<CustomizeProducts>
    private lateinit var daftarProdukListBesar: ArrayList<CustomizeProducts>
    private lateinit var daftarListProduct: ArrayList<CustomizeProducts>
    private lateinit var adapter: MainCustomizeProdukPembeliAdapter
    private var sectionList: ArrayList<SectionCustomize> = ArrayList()
    private var countProdukBesar by Delegates.notNull<Int>()
    private var countProdukSedang by Delegates.notNull<Int>()
    private var countProdukKecil by Delegates.notNull<Int>()
    private var countTotalProduk by Delegates.notNull<Int>()
    private lateinit var hashTempp: HashMap<String, Int>
    private lateinit var tempHash: HashMap<String, Int>
    private var checkGiftbox by Delegates.notNull<Int>()
    private var countTotalPembayaranProduk by Delegates.notNull<Long>()
    private var countTotalPembayaranGiftbox by Delegates.notNull<Long>()
    private lateinit var namesProduct: String
    private lateinit var nextBottom : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_produk_pembeli)

        reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize")

        totalCustomize = findViewById(R.id.totalCustomize)
        totalProduk = findViewById(R.id.totalProduk)
        daftarProduk = findViewById(R.id.daftarProduk)
        totalPrice = findViewById(R.id.totalPrice)
        nextBottom = findViewById(R.id.totalCustomize)

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        mainRecyclerView = findViewById(R.id.mainRecyclerView)
        mainRecyclerView.setHasFixedSize(true)
        countProdukBesar = 0
        countProdukSedang = 0
        countProdukKecil = 0
        countTotalProduk = 0
        hashTempp = hashMapOf()
        hashTempp.put("kecil", 0)
        hashTempp.put("sedang", 0)
        hashTempp.put("besar", 0)
        checkGiftbox = 0
        tempHash = hashMapOf()
        countTotalPembayaranProduk = 0
        countTotalPembayaranGiftbox = 0
        namesProduct = ""

        nextBottom.setOnClickListener {
            if (checkGiftbox == 0){
                alertDialog("GAGAL!", "Tambah produk terlebih dahulu!", false)
            }
        }

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarProdukListKecil = arrayListOf<CustomizeProducts>()
        daftarProdukListSedang = arrayListOf<CustomizeProducts>()
        daftarProdukListBesar = arrayListOf<CustomizeProducts>()
        daftarListProduct = arrayListOf<CustomizeProducts>()

        showListProduct()

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun showListProduct() {
        reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProdukListBesar.clear()
                    daftarProdukListSedang.clear()
                    daftarProdukListKecil.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(CustomizeProducts::class.java)!!
                        if (products.kategoriProduct == "kecil"){
                            daftarProdukListKecil.add(products)
                        } else if (products.kategoriProduct == "sedang"){
                            daftarProdukListSedang.add(products)
                        } else if (products.kategoriProduct == "besar"){
                            daftarProdukListBesar.add(products)
                        }
                    }

                    val sectionOneName = "Ukuran Kecil"
                    val sectionTwoName = "Ukuran Sedang"
                    val sectionThreeName = "Ukuran Besar"

                    sectionList.add(SectionCustomize(sectionOneName, daftarProdukListKecil))
                    sectionList.add(SectionCustomize(sectionTwoName, daftarProdukListSedang))
                    sectionList.add(SectionCustomize(sectionThreeName, daftarProdukListBesar))

                    adapter = MainCustomizeProdukPembeliAdapter(sectionList)
                    mainRecyclerView.adapter = adapter
                    mainRecyclerView.addItemDecoration(
                        DividerItemDecoration(
                            this@CustomizeProdukPembeliActivity,
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    adapter.setOnItemClickCallback(object : MainCustomizeProdukPembeliAdapter.OnItemClickCallback{
                        override fun onItemClicked(
                            data: ArrayList<CustomizeProducts>,
                            hashTemp: HashMap<String, Int>,
                            totalProduk: HashMap<String, Int>
                        ) {
                            if (hashTemp.containsKey("kecil")){
                                val count = hashTemp.getValue("kecil")
                                hashTempp.put("kecil", count)
                            } else if (hashTemp.containsKey("sedang")){
                                val count = hashTemp.getValue("sedang")
                                hashTempp.put("sedang", count)
                            } else{
                                val count = hashTemp.getValue("besar")
                                hashTempp.put("besar", count)
                            }
                            ruleBasedGiftbox(hashTempp)
                            getTotalPembayaranProduk(totalProduk)
                            totalCustomize.visibility = View.VISIBLE
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getTotalPembayaranProduk(totalProduk: HashMap<String, Int>){
        val reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarListProduct.clear()
                    countTotalPembayaranProduk = 0
                    namesProduct = ""
                    for (i in snapshot.children){
                        val products = i.getValue(CustomizeProducts::class.java)!!
                        if (products.idProduct in totalProduk.keys){
                            daftarListProduct.add(products)
                            countTotalPembayaranProduk += totalProduk.getValue(products.idProduct) * products.hargaProduct
                            namesProduct = "$namesProduct${products.namaProduct}, "
                        }
                    }
                }
                val formatter: NumberFormat = DecimalFormat("#,###")
                if (checkGiftbox == 0){
                    totalPrice.text = "Tidak dapat diproses"
                    daftarProduk.text = "produk"
                } else if (checkGiftbox == 1){
                    countTotalPembayaranGiftbox = 13000
                    countTotalPembayaranProduk += 13000
                    val formattedNumberPrice: String = formatter.format(countTotalPembayaranProduk)
                    totalPrice.text = "Rp. $formattedNumberPrice"
                    daftarProduk.text = "${namesProduct}"
                } else if (checkGiftbox == 2){
                    countTotalPembayaranGiftbox = 15000
                    countTotalPembayaranProduk += 15000
                    val formattedNumberPrice: String = formatter.format(countTotalPembayaranProduk)
                    totalPrice.text = "Rp. $formattedNumberPrice"
                    daftarProduk.text = "${namesProduct}"
                } else if (checkGiftbox == 3){
                    countTotalPembayaranGiftbox = 17000
                    countTotalPembayaranProduk += 17000
                    val formattedNumberPrice: String = formatter.format(countTotalPembayaranProduk)
                    totalPrice.text = "Rp. $formattedNumberPrice"
                    daftarProduk.text = "${namesProduct}"
                } else if (checkGiftbox == 4){
                    countTotalPembayaranGiftbox = 21000
                    countTotalPembayaranProduk += 21000
                    val formattedNumberPrice: String = formatter.format(countTotalPembayaranProduk)
                    totalPrice.text = "Rp. $formattedNumberPrice"
                    daftarProduk.text = "${namesProduct}"
                } else {
                    totalPrice.text = "Tidak dapat diproses"
                    daftarProduk.text = "produk"
                }

                nextBottom.setOnClickListener {
                    if (checkGiftbox == 0){
                        alertDialog("GAGAL!", "Transaksi tidak dapat diproses!", false)
                    } else if (checkGiftbox == 5){
                        alertDialog("GAGAL!", "Transaksi tidak dapat diproses!", false)
                    } else {
                        Intent(applicationContext, DetailCustomizeProdukPembeliActivity::class.java).also {
                            it.putExtra(EXTRA_PRODUK, daftarListProduct)
                            it.putExtra(EXTRA_ID_PRODUK, totalProduk)
                            it.putExtra(EXTRA_HARGA_GIFTBOX, countTotalPembayaranGiftbox)
                            startActivity(it)
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                            finish()
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun ruleBasedGiftbox(hashTemp: HashMap<String, Int>){
        countProdukBesar = hashTemp.getValue("besar")
        countProdukSedang = hashTemp.getValue("sedang")
        countProdukKecil = hashTemp.getValue("kecil")
        countTotalProduk = countProdukKecil + countProdukSedang + countProdukBesar

        totalProduk.text = "$countTotalProduk produk"

        if (countTotalProduk > 10){
            checkGiftbox = 0
            alertDialog("GAGAL!", "Jumlah produk yang anda pilih lebih dari 10 produk, tolong kurangi jumlah pemilihan produk anda agar kustomisasi bisa dilakukan!", false)
        } else if (countTotalProduk == 0){
            checkGiftbox = 0
            totalCustomize.visibility = View.INVISIBLE
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 1 && countProdukKecil == 0){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 1 && countProdukKecil <= 4){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 1 && countProdukKecil <= 8){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 2 && countProdukKecil == 0){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 2 && countProdukKecil == 1){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 2 && countProdukKecil <= 7){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 3 && countProdukKecil == 0){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 3 && countProdukKecil <= 6){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 4 && countProdukKecil == 0){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 4 && countProdukKecil <= 5){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang > 4){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukKecil <= 4){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukKecil <= 9){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 1){
            checkGiftbox = 3
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 1 && countProdukKecil <= 5){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 1 && countProdukKecil > 5){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 2 && countProdukKecil <= 3){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 2 && countProdukKecil > 3){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 2){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang > 2){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukKecil <= 8){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 1 && countProdukKecil <= 3){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 1 && countProdukKecil > 3){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 2 && countProdukKecil >= 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 2){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang > 2){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukKecil <= 5){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukKecil > 5){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 3){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang == 1 && countProdukKecil == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang == 1 && countProdukKecil > 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang > 1){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukKecil <= 2){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukKecil > 2){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 4){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 5 && countProdukSedang >= 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 5 && countProdukKecil >= 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 5){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar > 5){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil == 0){
            checkGiftbox = 1
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil <= 4){
            checkGiftbox = 1
        } else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil <= 7){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil <= 9){
            checkGiftbox = 3
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil == 0){
            checkGiftbox = 1
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil == 1){
            checkGiftbox = 1
        } else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil <= 4){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil <= 7){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil <= 8){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 3  && countProdukKecil == 0){
            checkGiftbox = 2
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 3 && countProdukKecil == 1){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukSedang == 3 && countProdukKecil <= 4){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukSedang == 3 && countProdukKecil <= 7){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 4  && countProdukKecil == 0){
            checkGiftbox = 3
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 4 && countProdukKecil <= 3){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukSedang == 4 && countProdukKecil > 3){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 5 && countProdukKecil == 0){
            checkGiftbox = 4
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 5 && countProdukKecil == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukSedang == 5 && countProdukKecil > 1){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 6 && countProdukKecil == 0){
            checkGiftbox = 4
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 6 && countProdukKecil > 1){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang > 6){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukKecil <= 4){
            checkGiftbox = 1
        } else if (countTotalProduk <= 10 && countProdukKecil <= 7){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukKecil <= 10){
            checkGiftbox = 3
        }

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
