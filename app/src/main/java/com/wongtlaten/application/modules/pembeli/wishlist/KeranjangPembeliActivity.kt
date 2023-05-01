package com.wongtlaten.application.modules.pembeli.wishlist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.CartProducts
import com.wongtlaten.application.core.OnCartClicked
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_ID_PRODUK
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_PRODUK
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates


class KeranjangPembeliActivity : AppCompatActivity() {

    private lateinit var reference : DatabaseReference
    private lateinit var referenceCart : DatabaseReference
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarProdukList: ArrayList<String>
    private lateinit var daftarCartProdukList: ArrayList<Products>
    private lateinit var tempProducts: ArrayList<Products>
    private lateinit var tempIdProducts: HashMap<String, Int>
    private lateinit var adapter: DaftarKeranjangProdukAdapter
    private lateinit var btnBeliInactivated : Button
    private lateinit var btnBeliActivated : Button
    private lateinit var checkbox1 : CheckBox
    private lateinit var textKeranjang : TextView
    private lateinit var textTotal : TextView
    private var countTotalHarga by Delegates.notNull<Long>()
    private var countTotal by Delegates.notNull<Long>()
    private var check by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keranjang_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        btnBeliInactivated = findViewById(R.id.btnBeliInactivated)
        btnBeliActivated = findViewById(R.id.btnBeliActivated)
        checkbox1 = findViewById(R.id.checkbox1)
        textKeranjang = findViewById(R.id.textKeranjang)
        textTotal = findViewById(R.id.textTotal)
        countTotalHarga = 0
        countTotal = 0
        check = false

        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        referenceCart = FirebaseDatabase.getInstance().getReference("dataCartProduk").child(userIdentity.uid)

        btnBeliInactivated.visibility = View.VISIBLE

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProdukh ke dalam "daftarProdukList" sebagai array list
        daftarProdukList = arrayListOf<String>()
        daftarCartProdukList = arrayListOf<Products>()
        tempProducts = arrayListOf<Products>()
        tempIdProducts = hashMapOf<String, Int>()
        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()
        showListCartProduct(false)

        checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btnBeliActivated.visibility = View.VISIBLE
                btnBeliInactivated.visibility = View.INVISIBLE
                showListCartProduct(true)
                countTotal = 0
                var tempArrayProducts : ArrayList<Products> = arrayListOf<Products>()
                for (i in 0..daftarCartProdukList.size-1){
                    if (daftarCartProdukList[i].stockProduct > 0){
                        tempArrayProducts.add(daftarCartProdukList[i])
                    }
                }
                for (i in 0..tempArrayProducts.size-1){
                    if (tempArrayProducts[i].jenisProduct == "flash sale"){
                        val promo = 100 - tempArrayProducts[i].hargaPromoProduct
                        val totalPromo = ((promo.toFloat()/100) * tempArrayProducts[i].hargaProduct)
                        countTotal += totalPromo.toLong()
                    } else {
                        countTotal += tempArrayProducts[i].hargaProduct
                    }
                }
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(countTotal)
                textTotal.text = "Rp. ${formattedNumberPrice}"
                btnBeliActivated.setOnClickListener {
                    Log.d("checkProduct", "$tempArrayProducts")
                }
            } else {
                btnBeliActivated.visibility = View.INVISIBLE
                btnBeliInactivated.visibility = View.VISIBLE
                showListCartProduct(false)
                textTotal.text = "Rp. 0"
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

    private fun showListProduct() {
        referenceCart.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProdukList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(CartProducts::class.java)!!
                        if (products != null){
                            daftarProdukList.add(products.idProduct)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showListCartProduct(isCheckedCheckbox: Boolean) {
        reference = FirebaseDatabase.getInstance().getReference("dataProduk")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarCartProdukList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)!!
                        if (products.idProduct in daftarProdukList && products.statusProduct != "deleted"){
                            daftarCartProdukList.add(products)
                        }
                    }
                }

                adapter = DaftarKeranjangProdukAdapter(daftarCartProdukList, isCheckedCheckbox)
                adapter.setOnItemClickCallback(object : DaftarKeranjangProdukAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: ArrayList<Products>) {
                        if (data.size > 0){
                            btnBeliActivated.visibility = View.VISIBLE
                        } else {
                            btnBeliActivated.visibility = View.INVISIBLE
                        }
                        countTotal = 0
                        tempIdProducts.clear()
                        tempProducts.clear()
                        for (i in 0..data.size-1){
                            //untuk harga
                            if (data[i].jenisProduct == "flash sale"){
                                val promo = 100 - data[i].hargaPromoProduct
                                val totalPromo = ((promo.toFloat()/100) * data[i].hargaProduct)
                                countTotal += totalPromo.toLong()
                            } else {
                                countTotal += data[i].hargaProduct
                            }
                            //untuk tempid ke pengiriman
                            if (data[i].idProduct !in tempIdProducts){
                                tempIdProducts.put(data[i].idProduct, 1)
                            } else {
                                var tempCount = tempIdProducts.getValue(data[i].idProduct) + 1
                                tempIdProducts.put(data[i].idProduct, tempCount)
                            }
                            //untuk daftar produk
                            if (data[i] !in tempProducts){
                                tempProducts.add(data[i])
                            }
                        }
                        val formatter: NumberFormat = DecimalFormat("#,###")
                        val formattedNumberPrice: String = formatter.format(countTotal)
                        textTotal.text = "Rp. ${formattedNumberPrice}"
                        btnBeliActivated.text = "BELI (${data.size})"
                        btnBeliActivated.setOnClickListener {
                            for (i in 0..tempIdProducts.size - 1){
                                if (tempIdProducts.getValue(tempProducts[i].idProduct) < tempProducts[i].minimumPemesananProduct){
                                    alertDialog("GAGAL!", "Minimum pembelian produk ${tempProducts[i].namaProduct} adalah ${tempProducts[i].minimumPemesananProduct} produk", false)
                                    check = false
                                    return@setOnClickListener
                                } else{
                                    check = true
                                }
                            }
                            if (check){
                                Intent(applicationContext, PengirimanPembeliActivity::class.java).also {
                                    it.putExtra(EXTRA_PRODUK, tempProducts)
                                    it.putExtra(EXTRA_ID_PRODUK, tempIdProducts)
                                    startActivity(it)
                                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                    finish()
                                }
                            }
                        }
                    }

                    override fun unCheckList(check: Boolean) {
                        if (check){
                            checkbox1.isChecked = false
                        }
                    }

                    override fun onDeleteProduct(idCartProduct: String) {
                        deleteDialog(idCartProduct)
                    }


                })
                rvDaftarProduk.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //fungsi untuk menampilkan alert dialog ketika ingin menghapus item
    private fun deleteDialog(idCartProduct: String){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Apakah anda yakin ingin menghapus produk tersebut dari keranjang anda?")
            setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                referenceCart.child(idCartProduct).removeValue().addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(context, "Produk tersebut berhasil di hapus", Toast.LENGTH_SHORT).show()
                        tempProducts.clear()
                        countTotal = 0
                        val formatter: NumberFormat = DecimalFormat("#,###")
                        val formattedNumberPrice: String = formatter.format(countTotal)
                        textTotal.text = "Rp. ${formattedNumberPrice}"
                        btnBeliActivated.visibility = View.INVISIBLE
                        showListProduct()
                        showListCartProduct(false)
                    } else {
                        Toast.makeText(context, "Produk tersebut gagal di hapus", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        alertDialog.show()
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

    // Fungsi ini digunakan untuk menampilkan dialog peringatan tidak tersambung ke internet,
    // jika tetep tidak connect ke internet maka tetap looping dialog tersebut
    private fun showInternetDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle("PERINGATAN!")
            setMessage("Tidak ada koneksi internet, mohon nyalakan mobile data/wifi anda terlebih dahulu")
            setIcon(R.drawable.ic_alert)
            setCancelable(false)
            setPositiveButton(
                "Coba lagi",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (!isConnected(this@KeranjangPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: KeranjangPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}