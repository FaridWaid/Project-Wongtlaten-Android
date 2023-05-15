package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse
import com.squareup.picasso.Picasso
import com.wongtlaten.application.LoginActivity
import com.wongtlaten.application.PrediksiOngkirActivity
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.*
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.home.FlashSaleActivity
import com.wongtlaten.application.modules.pembeli.wishlist.KeranjangPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliActivity
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class ProfilePembeliFragment : Fragment() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    // Mendefinisikan variabel global dari view
    private lateinit var textName: TextView
    private lateinit var textTransaksi: TextView
    private lateinit var textTotal: TextView
    private lateinit var photoProfil: CircleImageView
    // Mendefinisikan variabel global dari view
    private lateinit var nextDataPribadi: ConstraintLayout
    private lateinit var nextKeamanan: ConstraintLayout
    private lateinit var nextPromosiProduk: ConstraintLayout
    private lateinit var nextKeranjangProduk: ConstraintLayout
    private lateinit var nextWishlistProduk: ConstraintLayout
    private lateinit var nextRiwayatTransaksi: ConstraintLayout
    private lateinit var nextKonfirmasiPembayaran: ConstraintLayout
    private lateinit var nextPrediksiOngkir: ConstraintLayout
    private lateinit var nextFaq: ConstraintLayout
    private lateinit var nextLogout: ConstraintLayout
    private lateinit var idUser: String
    private lateinit var newUpdateTransaction : String
    private var totalPembelianUser by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_pembeli, container, false)
    }

    override fun onResume() {
        super.onResume()
        keepData()
        updateTransaction()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!
        idUser = userIdentity.uid
        newUpdateTransaction = ""

        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        textName = view.findViewById(R.id.nameAccount)
        textTransaksi = view.findViewById(R.id.transaction)
        textTotal = view.findViewById(R.id.countTotal)
        photoProfil = view.findViewById(R.id.ivProfile)
        nextDataPribadi = view.findViewById(R.id.layoutDataPribadi)
        nextKeamanan = view.findViewById(R.id.layoutKeamanan)
        nextPromosiProduk = view.findViewById(R.id.layoutPromosiProduk)
        nextKeranjangProduk = view.findViewById(R.id.layoutKeranjangProduk)
        nextWishlistProduk = view.findViewById(R.id.layoutWishlistProduk)
        nextRiwayatTransaksi = view.findViewById(R.id.layoutRiwayatTransaksi)
        nextKonfirmasiPembayaran = view.findViewById(R.id.layoutKonfirmasiPembayaran)
        nextPrediksiOngkir = view.findViewById(R.id.layoutPrediksiOngkir)
        nextFaq = view.findViewById(R.id.layoutFaq)
        nextLogout = view.findViewById(R.id.layoutLogout)
        daftarTransaksi = arrayListOf()
        totalPembelianUser = 0

        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(userIdentity.uid)

        // Memanggil fungsi loadingBar dan mengeset time = 1000
        loadingBar(1000)

        // Memanggil fungsi keepData
        keepData()

        // Mendefinisikan variabel nextDataPribadi
        // overridePendingTransition digunakan untuk animasi dari intent
        nextDataPribadi.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileDataPribadiPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, ProfileDataPribadiPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextKeamanan.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, ProfileKeamananPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextPromosiProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, FlashSaleActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextKeranjangProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, KeranjangPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextWishlistProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            val navController = view.findNavController()
            navController.navigate(R.id.nav_wishlist)

        }

        nextKonfirmasiPembayaran.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, PembayaranPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextRiwayatTransaksi.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, RiwayatTransaksiPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextPrediksiOngkir.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, PrediksiOngkirActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextFaq.setOnClickListener {
            // Jika berhasil maka akan pindah ke ProfileKeamananPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, FaqPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextLogout.setOnClickListener {
            auth.signOut()
            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
                finish()
            }
        }

    }

    private fun keepData() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                textName.text = users.username
                textTransaksi.text = users.jumlahTransaksi.toString()
                Picasso.get().load(users.photoProfil).into(photoProfil)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
        val referen2 = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        referen2.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    totalPembelianUser = 0
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.idUser == idUser && transaction.statusPembayaran == "settlement"){
                            totalPembelianUser += transaction.totalPembayaran.toLong()
                        }
                    }
                    val formatter: NumberFormat = DecimalFormat("#,###")
                    val formattedNumberPrice: String = formatter.format(totalPembelianUser)
                    textTotal.text = "Rp. ${formattedNumberPrice}"
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
                        if (transaction.idUser == idUser && transaction.statusPembayaran == "pending"){
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
                                    var updateTransaction = Transaction(idUser, daftarTransaksi[i].idTransaksi, daftarTransaksi[i].jenisTransaksi, daftarTransaksi[i].namePenerima, daftarTransaksi[i].kotaTujuan, daftarTransaksi[i].kodePos, daftarTransaksi[i].alamatLengkap, daftarTransaksi[i].teleponPenerima, daftarTransaksi[i].totalBerat, daftarTransaksi[i].jumlahOngkir, daftarTransaksi[i].totalPembayaran, daftarTransaksi[i].typePembayaran, daftarTransaksi[i].waktuTransaksi, daftarTransaksi[i].waktuPengiriman, newUpdateTransaction, daftarTransaksi[i].statusProduk, daftarTransaksi[i].kurir, daftarTransaksi[i].resiPengiriman, daftarTransaksi[i].catatanGiftcard, daftarTransaksi[i].pdfUrl, daftarTransaksi[i].produkTransaction)
                                    reference.child(daftarTransaksi[i].idTransaksi).setValue(updateTransaction)
                                } else{
                                    if (newUpdateTransaction == "settlement"){
                                        updatePembelianUser(idUser)
                                    }
                                    var updateTransaction = Transaction(idUser, daftarTransaksi[i].idTransaksi, daftarTransaksi[i].jenisTransaksi, daftarTransaksi[i].namePenerima, daftarTransaksi[i].kotaTujuan, daftarTransaksi[i].kodePos, daftarTransaksi[i].alamatLengkap, daftarTransaksi[i].teleponPenerima, daftarTransaksi[i].totalBerat, daftarTransaksi[i].jumlahOngkir, daftarTransaksi[i].totalPembayaran, daftarTransaksi[i].typePembayaran, daftarTransaksi[i].waktuTransaksi, daftarTransaksi[i].waktuPengiriman, newUpdateTransaction, daftarTransaksi[i].statusProduk, daftarTransaksi[i].kurir, daftarTransaksi[i].resiPengiriman, daftarTransaksi[i].catatanGiftcard, daftarTransaksi[i].pdfUrl, daftarTransaksi[i].produkTransaction)
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

    // Membuat fungsi "loadingBar" dengan parameter time,
    // Fungsi ini digunakan untuk menampilkan loading dialog
    private fun loadingBar(time: Long) {
        val loading = LoadingDialog(requireActivity())
        loading.startDialog()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                loading.isDissmis()
            }

        }, time)
    }

}