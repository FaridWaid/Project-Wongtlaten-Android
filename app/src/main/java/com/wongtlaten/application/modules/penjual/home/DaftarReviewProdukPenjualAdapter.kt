package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.KomentarProdukPenjualActivity.Companion.EXTRA_ID_PRODUCT
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarReviewProdukPenjualAdapter(private var list: ArrayList<Products>): RecyclerView.Adapter<DaftarReviewProdukPenjualAdapter.DaftarProdukViewHolder>() {

    // Membuat class DaftarProdukViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarProdukViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val nameProduk: TextView = itemView.findViewById(R.id.nameProduk)
        val hargaProduk: TextView = itemView.findViewById(R.id.price)
        val promoProduk: TextView = itemView.findViewById(R.id.pricePromo)
        val btnCekReview: Button = itemView.findViewById(R.id.btnCekReview)
        fun bind(produk: Products){
            with(itemView){
                Picasso.get().load(produk.photoProduct1).into(imageProduk)
                nameProduk.text = produk.namaProduct
                val formatter: NumberFormat = DecimalFormat("#,###")
                val price = produk.hargaProduct
                val promo = 100 - produk.hargaPromoProduct
                val totalPromo = ((promo.toFloat()/100) * produk.hargaProduct)
                val formattedNumberPrice: String = formatter.format(price)
                val formattedNumberPrice2: String = formatter.format(totalPromo.toLong())
                hargaProduk.text = "Rp. ${formattedNumberPrice}"
                if (produk.jenisProduct == "flash sale"){
                    hargaProduk.paintFlags = hargaProduk.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                    promoProduk.visibility = View.VISIBLE
                    promoProduk.text = "Rp. ${formattedNumberPrice2}"
                } else {
                    promoProduk.visibility = View.INVISIBLE
                }
                btnCekReview.setOnClickListener {
                    val moveIntent = Intent(itemView.context, KomentarProdukPenjualActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_PRODUCT, produk.idProduct)
                    itemView.context.startActivity(moveIntent)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_review, parent, false)
        return DaftarProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarProdukViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    //Fungsi ini digunakan untuk memasukkan data ke dalam list
    fun filterList(filteredNames: ArrayList<Products>) {
        this.list = filteredNames
        notifyDataSetChanged()
    }
}