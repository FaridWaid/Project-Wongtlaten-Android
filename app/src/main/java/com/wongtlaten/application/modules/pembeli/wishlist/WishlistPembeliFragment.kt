package com.wongtlaten.application.modules.pembeli.wishlist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.home.FlashSaleActivity
import com.wongtlaten.application.modules.pembeli.home.FsAdapter

class WishlistPembeliFragment : Fragment() {

    private lateinit var rvDaftarProduk: RecyclerView
    private lateinit var daftarFsList: ArrayList<Products>
    private lateinit var adapterFs: FsAdapter
    private lateinit var itemFiturCart: CardView
    private lateinit var itemFiturPayment: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist_pembeli, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemFiturCart = view.findViewById(R.id.itemFiturCart)
        itemFiturPayment = view.findViewById(R.id.itemFiturPayment)

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
        daftarFsList = arrayListOf<Products>()

        adapterFs = FsAdapter(daftarFsList)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduct()

    }

    private fun showListProduct() {
        val ref = FirebaseDatabase.getInstance().getReference("dataProduk")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarFsList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)
                        if (products?.jenisProduct == "flash sale"){
                            daftarFsList.add(products)
                        }
                    }

                    adapterFs = FsAdapter(daftarFsList)
                    rvDaftarProduk.adapter = adapterFs
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}