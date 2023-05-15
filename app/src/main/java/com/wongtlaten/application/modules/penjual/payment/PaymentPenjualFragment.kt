package com.wongtlaten.application.modules.penjual.payment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliAdapter
import com.wongtlaten.application.modules.penjual.payment.DetailPaymentPenjualActivity.Companion.EXTRA_TRANSACTION
import com.wongtlaten.application.modules.penjual.profile.ProfileDataPribadiPenjualActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentPenjualFragment : Fragment() {

    private lateinit var reference : DatabaseReference
    private lateinit var produkNotFound : LinearLayout
    private lateinit var rvDaftarPayment : RecyclerView
    private lateinit var adapter: PaymentPenjualAdapter
    private lateinit var daftarPayment: ArrayList<Transaction>
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    private lateinit var newUpdateTransaction : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_penjual, container, false)
    }

    override fun onResume() {
        super.onResume()
        updateTransaction()
//        if (daftarTransaksi.size != daftarPayment.size){
//            updateTransaction()
//        } else{
//            showListPayment()
//        }
        showListPayment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        produkNotFound = view.findViewById(R.id.produkNotFound)
        rvDaftarPayment = view.findViewById(R.id.rvDaftarPayment)
        daftarPayment = arrayListOf()
        daftarTransaksi = arrayListOf()
        newUpdateTransaction = ""

        updateTransaction()
//        if (daftarTransaksi.size != daftarPayment.size){
//            updateTransaction()
//        } else{
//            showListPayment()
//        }
        showListPayment()

    }

    private fun showListPayment(){
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarPayment.clear()
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.statusPembayaran == "pending"){
                            daftarPayment.add(transaction)
                        }
                    }
                }
                if (daftarPayment.isEmpty()){
                    produkNotFound.visibility = View.VISIBLE
                } else {
                    rvDaftarPayment.visibility = View.VISIBLE
                    // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                    rvDaftarPayment.setHasFixedSize(true)
                    rvDaftarPayment.layoutManager = LinearLayoutManager(context)
                    adapter = PaymentPenjualAdapter(daftarPayment)
                    adapter.setOnItemClickCallback(object : PaymentPenjualAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: ArrayList<Transaction>) {
                            requireActivity().run{
                                startActivity(Intent(this, DetailPaymentPenjualActivity::class.java).also {
                                    it.putExtra(EXTRA_TRANSACTION, data)
                                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                })
                            }
                        }

                    })
                    rvDaftarPayment.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateTransaction() {
        val reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarTransaksi.clear()
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.statusPembayaran == "pending"){
                            daftarTransaksi.add(transaction)
                        }
                    }
                }
                for (i in 0..daftarTransaksi.size-1){
                    RetrofitClient.instance.getStatusTransaction(daftarTransaksi[i].idTransaksi).enqueue(object:
                        Callback<TransactionStatusResponse> {
                        override fun onResponse(
                            call: Call<TransactionStatusResponse>,
                            response: Response<TransactionStatusResponse>
                        ) {
                            newUpdateTransaction = response.body()?.transactionStatus ?: "pending"
                            if (daftarTransaksi.isNotEmpty()){
                                if (daftarTransaksi[i].statusPembayaran != newUpdateTransaction && newUpdateTransaction == "expire"){
                                    if (daftarTransaksi[i].jenisTransaksi == "custom"){
                                        for (j in 0..daftarTransaksi[i].produkTransaction.size - 1){
                                            var referenceCustom = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(daftarTransaksi[i].produkTransaction[j].idProduk)
                                            val menuListener = object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    val produk = dataSnapshot.getValue(CustomizeProducts::class.java)!!
                                                    var productUpdateCustomize = CustomizeProducts(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct + daftarTransaksi[i].produkTransaction[j].totalBeli, produk.beratProduct, produk.panjangProduct, produk.lebarProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.photoProduct1, produk.statusProduct)
                                                    referenceCustom.setValue(productUpdateCustomize)
                                                }
                                                override fun onCancelled(databaseError: DatabaseError) {
                                                    // handle error
                                                }
                                            }
                                            referenceCustom.addListenerForSingleValueEvent(menuListener)
                                        }
                                    } else{
                                        for (j in 0..daftarTransaksi[i].produkTransaction.size - 1){
                                            var referenceNormal = FirebaseDatabase.getInstance().getReference("dataProduk").child(daftarTransaksi[i].produkTransaction[j].idProduk)
                                            val menuListener = object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    val produk = dataSnapshot.getValue(Products::class.java)!!
                                                    var productUpdateNormal = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct + daftarTransaksi[i].produkTransaction[j].totalBeli, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, produk.ratingProduct, produk.jumlahPembelianProduct - daftarTransaksi[i].produkTransaction[j].totalBeli, produk.statusProduct)
                                                    referenceNormal.setValue(productUpdateNormal)
                                                }
                                                override fun onCancelled(databaseError: DatabaseError) {
                                                    // handle error
                                                }
                                            }
                                            referenceNormal.addListenerForSingleValueEvent(menuListener)
                                        }
                                    }
                                    var updateTransaction = Transaction(daftarTransaksi[i].idUser, daftarTransaksi[i].idTransaksi, daftarTransaksi[i].jenisTransaksi, daftarTransaksi[i].namePenerima, daftarTransaksi[i].kotaTujuan, daftarTransaksi[i].kodePos, daftarTransaksi[i].alamatLengkap, daftarTransaksi[i].teleponPenerima, daftarTransaksi[i].totalBerat, daftarTransaksi[i].jumlahOngkir, daftarTransaksi[i].totalPembayaran, daftarTransaksi[i].typePembayaran, daftarTransaksi[i].waktuTransaksi, daftarTransaksi[i].waktuPengiriman, newUpdateTransaction, daftarTransaksi[i].statusProduk, daftarTransaksi[i].kurir, daftarTransaksi[i].resiPengiriman, daftarTransaksi[i].catatanGiftcard, daftarTransaksi[i].pdfUrl, daftarTransaksi[i].produkTransaction)
                                    reference.child(daftarTransaksi[i].idTransaksi).setValue(updateTransaction)
                                } else{
                                    if (newUpdateTransaction == "settlement"){
                                        updatePembelianUser(daftarTransaksi[i].idUser)
                                    }
                                    var updateTransaction = Transaction(daftarTransaksi[i].idUser, daftarTransaksi[i].idTransaksi, daftarTransaksi[i].jenisTransaksi, daftarTransaksi[i].namePenerima, daftarTransaksi[i].kotaTujuan, daftarTransaksi[i].kodePos, daftarTransaksi[i].alamatLengkap, daftarTransaksi[i].teleponPenerima, daftarTransaksi[i].totalBerat, daftarTransaksi[i].jumlahOngkir, daftarTransaksi[i].totalPembayaran, daftarTransaksi[i].typePembayaran, daftarTransaksi[i].waktuTransaksi, daftarTransaksi[i].waktuPengiriman, newUpdateTransaction, daftarTransaksi[i].statusProduk, daftarTransaksi[i].kurir, daftarTransaksi[i].resiPengiriman, daftarTransaksi[i].catatanGiftcard, daftarTransaksi[i].pdfUrl, daftarTransaksi[i].produkTransaction)
                                    reference.child(daftarTransaksi[i].idTransaksi).setValue(updateTransaction)
                                }
                            }
                        }

                        override fun onFailure(call: Call<TransactionStatusResponse>, t: Throwable) {

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updatePembelianUser(idUser: String){
        val referenceUser = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(idUser)
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                val usersUpdate = Users(users.idUsers, users.username, users.kelamin, users.alamat, users.email, users.photoProfil, users.noTelp, users.jumlahTransaksi + 1, users.accessLevel, users.token, users.status, users.checkOtp)
                referenceUser.setValue(usersUpdate)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        referenceUser.addListenerForSingleValueEvent(menuListener)
    }

}