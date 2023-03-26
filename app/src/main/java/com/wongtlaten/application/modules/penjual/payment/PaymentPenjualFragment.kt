package com.wongtlaten.application.modules.penjual.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.R

class PaymentPenjualFragment : Fragment() {

    private lateinit var produkNotFound : LinearLayout
    private lateinit var rvDaftarProduk : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_penjual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        produkNotFound = view.findViewById(R.id.produkNotFound)
        rvDaftarProduk = view.findViewById(R.id.rvDaftarProduk)

        produkNotFound.visibility = View.VISIBLE

    }

}