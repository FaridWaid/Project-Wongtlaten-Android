package com.wongtlaten.application.modules.pembeli.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity.Companion.EXTRA_ID_PRODUCT
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualActivity
import com.wongtlaten.application.modules.penjual.home.DaftarProdukPenjualAdapter
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarProdukAdapter(private var list: ArrayList<Products>, val method: SearchPembeliActivity): RecyclerView.Adapter<DaftarProdukAdapter.DaftarProdukViewHolder>() {

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarProdukViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val typeProduk: TextView = itemView.findViewById(R.id.typeProduk)
        val namaProduk: TextView = itemView.findViewById(R.id.nameProduk)
        fun bind(products: Products){
            with(itemView){
                Picasso.get().load(products.photoProduct1).into(imageProduk)
                typeProduk.text = products.kategoriProduct
                namaProduk.text = products.namaProduct
                itemView.setOnClickListener {
                    val moveIntent = Intent(itemView.context, DetailProdukPembeliActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_PRODUCT, products.idProduct)
                    itemView.context.startActivity(moveIntent)
                    method.animationToLeft()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_search, parent, false)
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