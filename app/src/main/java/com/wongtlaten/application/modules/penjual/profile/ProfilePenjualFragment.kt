package com.wongtlaten.application.modules.penjual.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity
import com.wongtlaten.application.modules.pembeli.profile.ProfileKeamananPembeliActivity

class ProfilePenjualFragment : Fragment() {

    // Mendefinisikan variabel global dari view
    private lateinit var nextDataPribadi: AppCompatImageView
    private lateinit var nextKeamanan: AppCompatImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_penjual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mendefinisikan variabel yang berisi view
        nextDataPribadi = view.findViewById(R.id.iconNextDataPribadi)
        nextKeamanan = view.findViewById(R.id.iconNextKeamanan)

        // Mendefinisikan variabel item fitur 2
        // overridePendingTransition digunakan untuk animasi dari intent
        nextDataPribadi.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarSampahAdminActivity
            requireActivity().run{
                startActivity(Intent(this, ProfileDataPribadiPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextKeamanan.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarSampahAdminActivity
            requireActivity().run{
                startActivity(Intent(this, ProfileKeamananPenjualActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

    }

}