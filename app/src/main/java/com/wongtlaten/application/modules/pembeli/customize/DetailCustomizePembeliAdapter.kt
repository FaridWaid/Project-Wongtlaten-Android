package com.wongtlaten.application.modules.pembeli.customize

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliAdapter
import java.text.DecimalFormat
import java.text.NumberFormat

class DetailCustomizePembeliAdapter(val list: ArrayList<CustomizeProducts>, val listIdProduk: HashMap<String, Int>): RecyclerView.Adapter<DetailCustomizePembeliAdapter.DaftarViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var tempProduct: ArrayList<String> = arrayListOf()
    private var countTotalProduct: Int = 0
    private var totalWeight: Int = 0
    private var countTotal: Long = 0
    private var totalProduk = 0

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: ImageView = itemView.findViewById(R.id.imageProduk)
        val namaProduk: TextView = itemView.findViewById(R.id.nameProduk)
        val countProduk: TextView = itemView.findViewById(R.id.countProduk)
        val priceProduk: TextView = itemView.findViewById(R.id.priceProduk)
        fun bind(products: CustomizeProducts){
            with(itemView){
                if (products.idProduct in listIdProduk.keys){
                    totalProduk = listIdProduk.getValue(products.idProduct)
                }
                val formatter: NumberFormat = DecimalFormat("#,###")
                val price = products.hargaProduct * totalProduk
                val formattedNumberPrice: String = formatter.format(price)
                priceProduk.text = "Rp. $formattedNumberPrice"
                countTotal += price
                Picasso.get().load(products.photoProduct1).into(imageProduk)
                namaProduk.text = products.namaProduct
                countProduk.text = "${totalProduk} produk (${products.beratProduct * totalProduk} kg)"
                totalWeight += products.beratProduct
                countTotalProduct += totalProduk
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_pengiriman, parent, false)
        return DaftarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        holder.bind(list[position])
        onItemClickCallback?.onItemClicked(countTotalProduct, totalWeight, countTotal)
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(countProduct: Int, totalWeight: Int, countTotal: Long)
    }

}