package com.wongtlaten.application.modules.pembeli.wishlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.*
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.home.ChatPembeliActivity
import com.wongtlaten.application.modules.pembeli.home.FlashSaleActivity
import com.wongtlaten.application.modules.pembeli.home.FsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class WishlistPembeliFragment : Fragment() {

    private lateinit var referenceCart : DatabaseReference
    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarWishlist: ArrayList<String>
    private lateinit var daftarProduct: ArrayList<Products>
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    private lateinit var adapterFs: DaftarWishlistProdukAdapter
    private lateinit var itemFiturChat: CardView
    private lateinit var itemFiturCart: CardView
    private lateinit var itemFiturPayment: CardView
    private lateinit var textChat : TextView
    private lateinit var textKeranjang : TextView
    private lateinit var textPayment : TextView
    private lateinit var idUser: String
    private lateinit var newUpdateTransaction : String
    private var countPayment by Delegates.notNull<Int>()
    private lateinit var daftarCartList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist_pembeli, container, false)
    }

    override fun onResume() {
        super.onResume()
        showListProduct()
        updateTransaction()
        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        idUser = userIdentity.uid
        val idPenjual = "UHS1kbdOPMeg0sug6zt0Xt8LUy33"
        updateNewChat(idPenjual, idUser)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        idUser = userIdentity.uid
        val idPenjual = "UHS1kbdOPMeg0sug6zt0Xt8LUy33"
        newUpdateTransaction = ""

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        referenceCart = FirebaseDatabase.getInstance().getReference("dataCartProduk").child(userIdentity.uid)

        itemFiturChat = view.findViewById(R.id.itemFiturChat)
        itemFiturCart = view.findViewById(R.id.itemFiturCart)
        itemFiturPayment = view.findViewById(R.id.itemFiturPayment)
        textChat = view.findViewById(R.id.textChat)
        textKeranjang = view.findViewById(R.id.textKeranjang)
        textPayment = view.findViewById(R.id.textPayment)
        daftarTransaksi = arrayListOf()
        daftarCartList = arrayListOf()
        countPayment = 0

        itemFiturChat.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, ChatPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturCart.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, KeranjangPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturPayment.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, PembayaranPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarProduk = view.findViewById(R.id.rvDaftarProduk)
        rvDaftarProduk.setHasFixedSize(true)
        rvDaftarProduk.layoutManager = GridLayoutManager(requireContext(), 2)

        // Memasukkan data DaftarProduk ke dalam "daftarFsList" sebagai array list
        daftarWishlist = arrayListOf<String>()
        daftarProduct = arrayListOf<Products>()

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()
        checkIsProductOnDatabase()
        updateNewChat(idPenjual, idUser)

    }

    private fun showListProduct() {
        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        val referenceWishlist = FirebaseDatabase.getInstance().getReference("dataWishlistProduk").child(userIdentity.uid)
        referenceWishlist.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarWishlist.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(WishlistProducts::class.java)
                        if (products != null){
                            daftarWishlist.add(products.idProduct)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val reference = FirebaseDatabase.getInstance().getReference("dataProduk")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProduct.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)!!
                        if (products.idProduct in daftarWishlist && products.statusProduct != "deleted"){
                            daftarProduct.add(products)
                        }
                    }
                }

                adapterFs = DaftarWishlistProdukAdapter(daftarProduct)
                rvDaftarProduk.adapter = adapterFs

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun checkIsProductOnDatabase(){
        referenceCart.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarCartList.clear()
                if (snapshot.exists()){
                    for (i in snapshot.children){
                        val carts = i.getValue(CartProducts::class.java)!!
                        daftarCartList.add(carts.idProduct)
                    }
                }
                val ref = FirebaseDatabase.getInstance().getReference("dataProduk")
                ref.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            var countCart = 0
                            for (i in snapshot.children){
                                val products = i.getValue(Products::class.java)!!
                                if (products.idProduct in daftarCartList && products.statusProduct != "deleted"){
                                    countCart += 1
                                }
                            }
                            if (countCart > 0){
                                textKeranjang.visibility = View.VISIBLE
                                textKeranjang.text = " $countCart "
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
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
                    countPayment = 0
                    daftarTransaksi.clear()
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.idUser == idUser && transaction.statusPembayaran == "pending"){
                            daftarTransaksi.add(transaction)
                            countPayment += 1
                        }
                    }
                    if (countPayment > 0){
                        textPayment.visibility = View.VISIBLE
                        textPayment.text = " $countPayment "
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
                            if (daftarTransaksi[i].statusPembayaran != newUpdateTransaction && newUpdateTransaction == "expire"){
                                if (daftarTransaksi[i].jenisTransaksi == "custom"){
                                    for (j in 0..daftarTransaksi[i].produkTransaction.size - 1){
                                        var referenceCustom = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(daftarTransaksi[i].produkTransaction[j].idProduk)
                                        val menuListener = object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                val produk = dataSnapshot.getValue(CustomizeProducts::class.java)!!
                                                var productUpdateCustomize = CustomizeProducts(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct + daftarTransaksi[i].produkTransaction[j].totalBeli, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.photoProduct1, produk.statusProduct)
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

                        override fun onFailure(call: Call<TransactionStatusResponse>, t: Throwable) {

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateNewChat(senderId: String, receiverId: String){
        val databaseReferenceChat: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("dataChatting").child(idUser)

        databaseReferenceChat.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var countNewChat = 0
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)!!
                    if (chat.senderId.equals(senderId) && chat.receiverId.equals(receiverId)
                    ) {
                        if (chat.statusMessage == "0"){
                            countNewChat += 1
                        }
                    }
                }
                if (countNewChat > 0){
                    textChat.visibility = View.VISIBLE
                    textChat.text = " $countNewChat "
                }
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