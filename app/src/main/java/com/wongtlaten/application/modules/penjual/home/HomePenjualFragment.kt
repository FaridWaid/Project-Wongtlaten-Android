package com.wongtlaten.application.modules.penjual.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse
import com.squareup.picasso.Picasso
import com.wongtlaten.application.PrediksiOngkirActivity
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class HomePenjualFragment : Fragment() {

    // Mendefinisikan variabel global dari view
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    private lateinit var fiturPengelolaanProduk: CardView
    private lateinit var fiturPengelolaanAkun: CardView
    private lateinit var fiturPengelolaanTransaksi: CardView
    private lateinit var fiturPengelolaanSaldo: CardView
    private lateinit var fiturKustomisasiProduk: CardView
    private lateinit var fiturReviewProduk: CardView
    private lateinit var fiturLaporan: CardView
    private lateinit var fiturNotifikasi: CardView
    private lateinit var fiturFaq: CardView
    private lateinit var fiturPrediksiOngkir: CardView
    private lateinit var profilePicture : ImageView
    private lateinit var btnCekSekarang : Button
    private lateinit var totalProductTerjual : TextView
    private lateinit var newUpdateTransaction : String
    private var countProdukTerjual by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_penjual, container, false)
    }

    override fun onResume() {
        super.onResume()
        keepData()
        updateTransaction()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fiturPengelolaanProduk = view.findViewById(R.id.itemFitur1)
        fiturPengelolaanAkun = view.findViewById(R.id.itemFitur2)
        fiturPengelolaanTransaksi = view.findViewById(R.id.itemFitur3)
        fiturPengelolaanSaldo = view.findViewById(R.id.itemFitur4)
        fiturKustomisasiProduk = view.findViewById(R.id.itemFitur5)
        fiturReviewProduk = view.findViewById(R.id.itemFitur6)
        fiturLaporan = view.findViewById(R.id.itemFitur7)
        fiturNotifikasi = view.findViewById(R.id.itemFitur8)
        fiturFaq = view.findViewById(R.id.itemFitur9)
        fiturPrediksiOngkir = view.findViewById(R.id.itemFitur10)
        profilePicture = view.findViewById(R.id.profilePicture)
        totalProductTerjual = view.findViewById(R.id.countProdukTerjual)
        btnCekSekarang = view.findViewById(R.id.btnCekSekarang)
        daftarTransaksi = arrayListOf()
        newUpdateTransaction = ""
        countProdukTerjual = 0

        // Memanggil fungsi keepData
        keepData()

        fiturPengelolaanProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarProdukPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturPengelolaanAkun.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarAkunPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturPengelolaanTransaksi.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarTransaksiPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturPengelolaanSaldo.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarSaldoPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturKustomisasiProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, KustomisasiProdukPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturReviewProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarReviewProdukPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturLaporan.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, LaporanPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturNotifikasi.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, NotifikasiPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturFaq.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarFaqPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        fiturPrediksiOngkir.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, PrediksiOngkirActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        btnCekSekarang.setOnClickListener {
            // Jika berhasil maka akan pindah ke KustomisasiProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarTransaksiPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

    }

    private fun keepData() {
        val userIdentiy = FirebaseAuth.getInstance().currentUser!!
        val referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(userIdentiy.uid)
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                Picasso.get().load(users.photoProfil).into(profilePicture)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
        val referen2 = FirebaseDatabase.getInstance().getReference("dataProduk")
        referen2.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    countProdukTerjual = 0
                    for (i in snapshot.children){
                        val product = i.getValue(Products::class.java)!!
                        if (product != null){
                            countProdukTerjual += product.jumlahPembelianProduct
                        }
                    }
                    totalProductTerjual.text = "${countProdukTerjual}"
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
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.statusPembayaran == "pending"){
                            daftarTransaksi.add(transaction)
                        }
                    }
                    okedeh(daftarTransaksi)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun okedeh(daftarTransaksi: ArrayList<Transaction>) {
        val reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        for (i in 0..daftarTransaksi.size-1){
            RetrofitClient.instance.getStatusTransaction(daftarTransaksi[i].idTransaksi).enqueue(object:
                Callback<TransactionStatusResponse> {
                override fun onResponse(
                    call: Call<TransactionStatusResponse>,
                    response: Response<TransactionStatusResponse>
                ) {
                    newUpdateTransaction = response.body()?.transactionStatus ?: "pending"
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
                                val menuListener2 = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val produk = dataSnapshot.getValue(Products::class.java)!!
                                        var productUpdateNormal = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct + daftarTransaksi[i].produkTransaction[j].totalBeli, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, produk.ratingProduct, produk.jumlahPembelianProduct - daftarTransaksi[i].produkTransaction[j].totalBeli, produk.statusProduct)
                                        referenceNormal.setValue(productUpdateNormal)
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // handle error
                                    }
                                }
                                referenceNormal.addListenerForSingleValueEvent(menuListener2)
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

                override fun onFailure(call: Call<TransactionStatusResponse>, t: Throwable) {

                }

            })
        }
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