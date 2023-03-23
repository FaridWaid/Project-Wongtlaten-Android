package com.wongtlaten.application.modules.penjual.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity

class HomePenjualFragment : Fragment() {

    // Mendefinisikan variabel global dari view
    private lateinit var fiturPengelolaanProduk: CardView
    private lateinit var fiturKustomisasiProduk: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_penjual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fiturPengelolaanProduk = view.findViewById(R.id.itemFitur1)
        fiturKustomisasiProduk = view.findViewById(R.id.itemFitur5)

        fiturPengelolaanProduk.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, DaftarProdukPenjualActivity::class.java))
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

    }

}