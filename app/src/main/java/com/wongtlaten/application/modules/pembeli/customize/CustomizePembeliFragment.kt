package com.wongtlaten.application.modules.pembeli.customize

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualActivity
import com.wongtlaten.application.modules.penjual.home.TambahKustomisasiProdukPenjualActivity

class CustomizePembeliFragment : Fragment() {

    private lateinit var cardTipe1: CardView
    private lateinit var cardTipe2: CardView
    private lateinit var cardTipe3: CardView
    private lateinit var cardTipe4: CardView
    private lateinit var btnCustomize: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customize_pembeli, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardTipe1 = view.findViewById(R.id.cardTipe1)
        cardTipe2 = view.findViewById(R.id.cardTipe2)
        cardTipe3 = view.findViewById(R.id.cardTipe3)
        cardTipe4 = view.findViewById(R.id.cardTipe4)
        btnCustomize = view.findViewById(R.id.btnCustomize)

        cardTipe1.setOnClickListener {
            showDialogTipe1()
        }

        cardTipe2.setOnClickListener {
            showDialogTipe2()
        }

        cardTipe3.setOnClickListener {
            showDialogTipe3()
        }

        cardTipe4.setOnClickListener {
            showDialogTipe4()
        }

        btnCustomize.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarProdukPenjualActivity
            requireActivity().run{
                startActivity(Intent(this, CustomizeProdukPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

    }

    private fun showDialogTipe1(){
        var dialog = Dialog(requireContext())
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_tipe1)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogTipe2(){
        var dialog = Dialog(requireContext())
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_tipe2)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogTipe3(){
        var dialog = Dialog(requireContext())
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_tipe3)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogTipe4(){
        var dialog = Dialog(requireContext())
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_tipe4)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}