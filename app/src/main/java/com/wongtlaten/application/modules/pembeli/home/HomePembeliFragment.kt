package com.wongtlaten.application.modules.pembeli.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity
import com.wongtlaten.application.modules.penjual.home.RecomViewPagerAdapter

class HomePembeliFragment : Fragment() {

    // Mendefinisikan variabel global dari view
    private lateinit var etSearch: EditText
    private lateinit var fsViewPager: ViewPager2
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_pembeli, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        val products = i.getValue(Products::class.java)
                        if (products?.jenisProduct == "popular"){
                            daftarPopularList.add(products)
                        } else if (products?.jenisProduct == "new"){
                            daftarNewList.add(products)
                        } else if (products?.jenisProduct == "flash sale"){
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


}