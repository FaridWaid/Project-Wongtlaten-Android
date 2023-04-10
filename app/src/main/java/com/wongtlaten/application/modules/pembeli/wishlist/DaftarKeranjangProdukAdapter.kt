package com.wongtlaten.application.modules.pembeli.wishlist

import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CartProducts
import com.wongtlaten.application.core.OnCartClicked
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarKeranjangProdukAdapter(private var list: ArrayList<Products>, private var  isCheckedCheckbox: Boolean): RecyclerView.Adapter<DaftarKeranjangProdukAdapter.DaftarProdukViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var tempProduct: ArrayList<Products> = arrayListOf()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarProdukViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val priceProduk: TextView = itemView.findViewById(R.id.price)
        val pricePromoProduk: TextView = itemView.findViewById(R.id.pricePromo)
        val namaProduk: TextView = itemView.findViewById(R.id.nameProduk)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val btnPlus: AppCompatImageView = itemView.findViewById(R.id.btnPlus)
        val btnMinus: AppCompatImageView = itemView.findViewById(R.id.btnMinus)
        val countProduk: TextView = itemView.findViewById(R.id.countProduk)
        val deleteProduk: AppCompatImageView = itemView.findViewById(R.id.deleteProduk)
        val closeProduk: AppCompatImageView = itemView.findViewById(R.id.closeProduk)
        fun bind(products: Products){
            with(itemView){
                Picasso.get().load(products.photoProduct1).into(imageProduk)
                namaProduk.text = products.namaProduct
                val formatter: NumberFormat = DecimalFormat("#,###")
                val price = products.hargaProduct
                val promo = 100 - products.hargaPromoProduct
                val totalPromo = ((promo.toFloat()/100) * products.hargaProduct)
                val formattedNumberPrice: String = formatter.format(price)
                val formattedNumberPrice2: String = formatter.format(totalPromo.toLong())
                priceProduk.text = "Rp. ${formattedNumberPrice}"
                if (products.jenisProduct == "flash sale"){
                    priceProduk.paintFlags = priceProduk.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                    pricePromoProduk.visibility = View.VISIBLE
                    pricePromoProduk.text = "Rp. ${formattedNumberPrice2}"
                } else {
                    pricePromoProduk.visibility = View.INVISIBLE
                }
                if (products.stockProduct <= 0){
                    checkbox.visibility = View.INVISIBLE
                    closeProduk.visibility = View.VISIBLE
                    btnPlus.setOnClickListener {
                        return@setOnClickListener
                    }
                    btnMinus.setOnClickListener {
                        return@setOnClickListener
                    }
                }
                if (isCheckedCheckbox){
                    if (products.stockProduct <= 0){
                        checkbox.visibility = View.INVISIBLE
                        closeProduk.visibility = View.VISIBLE
                        btnPlus.setOnClickListener {
                            return@setOnClickListener
                        }
                        btnMinus.setOnClickListener {
                            return@setOnClickListener
                        }
                    } else {
                        checkbox.isChecked = true
                        tempProduct.add(products)
                        onItemClickCallback?.onItemClicked(tempProduct)
                        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (!isChecked) {
                                onItemClickCallback?.unCheckList(true)
                                tempProduct.removeAll(tempProduct)
                                onItemClickCallback?.onItemClicked(tempProduct)
                            }
                        }
                    }
                } else {
                    checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            tempProduct.add(products)
                            onItemClickCallback?.onItemClicked(tempProduct)
                        } else{
                            tempProduct.remove(products)
                            onItemClickCallback?.onItemClicked(tempProduct)
                        }
                    }
                }
                var countProducts = 1
                btnPlus.setOnClickListener {
                    if (countProducts < products.stockProduct){
                        checkbox.isChecked = true
                        countProducts += 1
                        countProduk.text = countProducts.toString()
                        tempProduct.add(products)
                        onItemClickCallback?.onItemClicked(tempProduct)
                    } else {
                        Toast.makeText(context, "Saat ini hanya tersedia ${products.stockProduct} produk", Toast.LENGTH_SHORT).show()
                    }
                }
                btnMinus.setOnClickListener {
                    if (countProducts > 1){
                        countProducts -= 1
                        countProduk.text = countProducts.toString()
                        tempProduct.remove(products)
                        onItemClickCallback?.onItemClicked(tempProduct)
                    } else {
                        Toast.makeText(context, "Minimal pembelian produk ini adalah 1 produk", Toast.LENGTH_SHORT).show()
                    }
                }
                deleteProduk.setOnClickListener {
                    onItemClickCallback?.onDeleteProduct(products.idProduct)
                }
                itemView.setOnClickListener {
                    val moveIntent = Intent(itemView.context, DetailProdukPembeliActivity::class.java)
                    moveIntent.putExtra(DetailProdukPembeliActivity.EXTRA_ID_PRODUCT, products.idProduct)
                    itemView.context.startActivity(moveIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_keranjang, parent, false)
        return DaftarProdukViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarProdukViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<Products>)
        fun unCheckList(check: Boolean)
        fun onDeleteProduct(idCartProduct: String)
    }

}