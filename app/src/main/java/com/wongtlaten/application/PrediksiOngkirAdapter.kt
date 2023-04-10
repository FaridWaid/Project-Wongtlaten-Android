package com.wongtlaten.application

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.TempCostOngkir
import com.wongtlaten.application.modules.pembeli.home.DaftarProdukAdapter
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity
import java.text.DecimalFormat
import java.text.NumberFormat

class PrediksiOngkirAdapter(private var list: ArrayList<TempCostOngkir>): RecyclerView.Adapter<PrediksiOngkirAdapter.DaftarProdukViewHolder>() {

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarProdukViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val roundedBackgroundTiki: CardView = itemView.findViewById(R.id.roundedBackgroundTiki)
        val roundedBackgroundPos: CardView = itemView.findViewById(R.id.roundedBackgroundPos)
        val roundedBackgroundJne: CardView = itemView.findViewById(R.id.roundedBackgroundJne)
        val jasaOngkir: TextView = itemView.findViewById(R.id.jasaOngkir)
        val typeOngkir: TextView = itemView.findViewById(R.id.typeOngkir)
        val priceOngkir: TextView = itemView.findViewById(R.id.priceOngkir)
        val rangeOngkir: TextView = itemView.findViewById(R.id.rangeOngkir)
        fun bind(ongkir: TempCostOngkir){
            with(itemView){
                if (ongkir.code_courier == "jne"){
                    roundedBackgroundJne.visibility = View.VISIBLE
                } else if (ongkir.code_courier == "pos"){
                    roundedBackgroundPos.visibility = View.VISIBLE
                } else {
                    roundedBackgroundTiki.visibility = View.VISIBLE
                }
                jasaOngkir.text = ongkir.service_courier
                typeOngkir.text = ongkir.description_courier
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(ongkir.cost_courier)
                priceOngkir.text = formattedNumberPrice
                rangeOngkir.text = "${ongkir.estimate} (hari)"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_ongkir, parent, false)
        return DaftarProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarProdukViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size
}