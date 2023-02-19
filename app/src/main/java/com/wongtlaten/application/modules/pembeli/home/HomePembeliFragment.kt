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
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity

class HomePembeliFragment : Fragment() {

    // Mendefinisikan variabel global dari view
    private lateinit var etSearch: EditText
    private lateinit var itemFiturChat: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_pembeli, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemFiturChat = view.findViewById(R.id.itemFiturChat)
        itemFiturChat.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarSampahAdminActivity
            requireActivity().run{
                startActivity(Intent(this, DetailProdukPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        etSearch = view.findViewById(R.id.et_search)
        etSearch.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarSampahAdminActivity
            requireActivity().run{
                startActivity(Intent(this, SearchPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

    }

}