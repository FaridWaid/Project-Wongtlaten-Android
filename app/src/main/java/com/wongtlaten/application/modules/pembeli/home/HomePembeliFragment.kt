package com.wongtlaten.application.modules.pembeli.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse
import com.wongtlaten.application.R
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.*
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.customize.CustomizeProdukPembeliActivity
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.KeranjangPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliAdapter
import com.wongtlaten.application.modules.penjual.home.RecomViewPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class HomePembeliFragment : Fragment() {

    // Mendefinisikan variabel global dari view
    private lateinit var referenceCart : DatabaseReference
    private lateinit var etSearch: EditText
    private lateinit var fsViewPager: ViewPager2
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    private lateinit var daftarFsList: ArrayList<Products>
    private lateinit var adapterFs: FsViewPagerAdapter
    private lateinit var seeAllFs: TextView
    private lateinit var newViewPager: ViewPager2
    private lateinit var daftarNewList: ArrayList<Products>
    private lateinit var adapterNew: NewViewPagerAdapter
    private lateinit var seeAllNew: TextView
    private lateinit var popularViewPager: ViewPager2
    private lateinit var daftarPopularList: ArrayList<Products>
    private lateinit var adapterPopular: PopularViewPagerAdapter
    private lateinit var seeAllPopular: TextView
    private lateinit var itemFiturChat: CardView
    private lateinit var itemFiturCart: CardView
    private lateinit var itemFiturPayment: CardView
    private lateinit var itemFiturGiftbox: CardView
    private lateinit var itemFiturBouquet: CardView
    private lateinit var itemFiturAcrylic: CardView
    private lateinit var itemFiturPigura: CardView
    private lateinit var btnCustom: Button
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
        return inflater.inflate(R.layout.fragment_home_pembeli, container, false)
    }

    override fun onResume() {
        super.onResume()
        showListProduk()
        checkIsProductOnDatabase()
        updateTransaction()
        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        idUser = userIdentity.uid
        val idPenjual = "kJKchPfDIjVh0xhh9mEyixSFoso1"
        updateNewChat(idPenjual, idUser)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        idUser = userIdentity.uid
        newUpdateTransaction = ""
        val idPenjual = "kJKchPfDIjVh0xhh9mEyixSFoso1"

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        referenceCart = FirebaseDatabase.getInstance().getReference("dataCartProduk").child(userIdentity.uid)

        itemFiturChat = view.findViewById(R.id.itemFiturChat)
        itemFiturCart = view.findViewById(R.id.itemFiturCart)
        itemFiturPayment = view.findViewById(R.id.itemFiturPayment)
        itemFiturGiftbox = view.findViewById(R.id.itemFiturGiftbox)
        itemFiturAcrylic = view.findViewById(R.id.itemFiturAcrylic)
        itemFiturBouquet = view.findViewById(R.id.itemFiturBouquet)
        itemFiturPigura = view.findViewById(R.id.itemFiturPigura)
        textChat = view.findViewById(R.id.textChat)
        textKeranjang = view.findViewById(R.id.textKeranjang)
        btnCustom = view.findViewById(R.id.btnCustom)
        textPayment = view.findViewById(R.id.textPayment)
        daftarTransaksi = arrayListOf()
        daftarCartList = arrayListOf()
        countPayment = 0

        fsViewPager = view.findViewById(R.id.flashSaleViewPager)
        fsViewPager.clipToPadding = false
        fsViewPager.clipChildren = false
        fsViewPager.offscreenPageLimit = 3
        fsViewPager.getChildAt(0)
        fsViewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        seeAllFs = view.findViewById(R.id.seeAllFs)

        newViewPager = view.findViewById(R.id.newViewPager)
        newViewPager.clipToPadding = false
        newViewPager.clipChildren = false
        newViewPager.offscreenPageLimit = 3
        newViewPager.getChildAt(0)
        newViewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        seeAllNew = view.findViewById(R.id.seeAllNew)

        popularViewPager = view.findViewById(R.id.popularViewPager)
        popularViewPager.clipToPadding = false
        popularViewPager.clipChildren = false
        popularViewPager.offscreenPageLimit = 3
        popularViewPager.getChildAt(0)
        popularViewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        seeAllPopular = view.findViewById(R.id.seeAllPopular)

        daftarFsList = arrayListOf<Products>()
        daftarNewList = arrayListOf<Products>()
        daftarPopularList = arrayListOf<Products>()

        showListProduk()
        checkIsProductOnDatabase()
        updateTransaction()
        updateNewChat(idPenjual, idUser)

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

        itemFiturGiftbox.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, GiftboxPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturPigura.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, PiguraPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturBouquet.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, BouquetPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturAcrylic.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, AcrylicPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        seeAllFs.setOnClickListener {
            // Jika berhasil maka akan pindah ke FlashSaleActivity
            requireActivity().run{
                startActivity(Intent(this, FlashSaleActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        seeAllNew.setOnClickListener {
            // Jika berhasil maka akan pindah ke NewActivity
            requireActivity().run{
                startActivity(Intent(this, NewActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        seeAllPopular.setOnClickListener {
            // Jika berhasil maka akan pindah ke PopularActivity
            requireActivity().run{
                startActivity(Intent(this, PopularActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        btnCustom.setOnClickListener {
            requireActivity().run{
                startActivity(Intent(this, CustomizeProdukPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        etSearch = view.findViewById(R.id.et_search)
        etSearch.setOnClickListener {
            // Jika berhasil maka akan pindah ke SearchPembeliActivity
            requireActivity().run{
                startActivity(Intent(this, SearchPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

    }

    // Membuat fungsi "showListProduk" yang digunakan untuk menampilkan data dari database ke dalam,
    // recyclerview
    fun showListProduk() {
        val ref = FirebaseDatabase.getInstance().getReference("dataProduk")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarFsList.clear()
                    daftarNewList.clear()
                    daftarPopularList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)!!
                        if (products.jenisProduct == "popular" && products.statusProduct != "deleted"){
                            daftarPopularList.add(products)
                        } else if (products.jenisProduct == "new" && products.statusProduct != "deleted"){
                            daftarNewList.add(products)
                        } else if (products.jenisProduct == "flash sale" && products.statusProduct != "deleted"){
                            daftarFsList.add(products)
                        }
                    }

                    adapterFs = FsViewPagerAdapter(daftarFsList, SearchPembeliActivity())
                    fsViewPager.adapter = adapterFs
                    adapterNew = NewViewPagerAdapter(daftarNewList, SearchPembeliActivity())
                    newViewPager.adapter = adapterNew
                    adapterPopular = PopularViewPagerAdapter(daftarPopularList, SearchPembeliActivity())
                    popularViewPager.adapter = adapterPopular

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


}