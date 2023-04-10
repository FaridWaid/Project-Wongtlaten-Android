package com.wongtlaten.application.modules.pembeli.home

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
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
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.WishlistProducts
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity.Companion.EXTRA_ID_PRODUCT
import java.text.DecimalFormat
import java.text.NumberFormat

class FsViewPagerAdapter(val list: ArrayList<Products>, val method: SearchPembeliActivity): RecyclerView.Adapter<FsViewPagerAdapter.DaftarViewHolder>() {

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val textFs: TextView = itemView.findViewById(R.id.textFs)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val textRate: TextView = itemView.findViewById(R.id.textRate)
        val typeProduk: TextView = itemView.findViewById(R.id.typeProduk)
        val namaProduk: TextView = itemView.findViewById(R.id.nameProduk)
        val priceProduk: TextView = itemView.findViewById(R.id.price)
        val pricePromo: TextView = itemView.findViewById(R.id.pricePromo)
        val loveInactive: ImageView = itemView.findViewById(R.id.loveInactivated)
        val loveActive: ImageView = itemView.findViewById(R.id.loveActivated)
        fun bind(products: Products){
            with(itemView){
                val formatter: NumberFormat = DecimalFormat("#,###")
                val myNumber = products.hargaProduct
                val formattedNumber: String = formatter.format(myNumber)
                priceProduk.text = "Rp. $formattedNumber"
                val price = products.hargaProduct
                val promo = 100 - products.hargaPromoProduct
                val totalPromo = ((promo.toFloat()/100) * products.hargaProduct)
                val formattedNumberPrice2: String = formatter.format(totalPromo.toLong())
                priceProduk.paintFlags = priceProduk.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                pricePromo.text = "Rp. ${formattedNumberPrice2}"
                Picasso.get().load(products.photoProduct1).into(imageProduk)
                textFs.text = " ${products.hargaPromoProduct}% "
                ratingBar.rating = products.ratingProduct
                textRate.text = "(${products.ratingProduct})"
                typeProduk.text = products.kategoriProduct
                namaProduk.text = products.namaProduct

                val auth = FirebaseAuth.getInstance()
                val userIdentity = auth.currentUser!!

                // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
                val referenceWishlist = FirebaseDatabase.getInstance().getReference("dataWishlistProduk").child(userIdentity.uid)
                referenceWishlist.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            for (i in snapshot.children){
                                val productWishlist = i.getValue(WishlistProducts::class.java)!!
                                if (productWishlist.idProduct == products.idProduct){
                                    loveActive.visibility = View.VISIBLE
                                } else {
                                    loveInactive.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

                loveInactive.setOnClickListener {
                    loveActive.visibility = View.VISIBLE
                    loveInactive.visibility = View.INVISIBLE
                    val cartUpdate = CartProducts(userIdentity.uid, products.idProduct)
                    referenceWishlist.child(products.idProduct).setValue(cartUpdate)
                }
                loveActive.setOnClickListener {
                    loveInactive.visibility = View.VISIBLE
                    loveActive.visibility = View.INVISIBLE
                    referenceWishlist.child(products.idProduct).removeValue()
                }

                itemView.setOnClickListener {
                    val moveIntent = Intent(itemView.context, DetailProdukPembeliActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_PRODUCT, products.idProduct)
                    itemView.context.startActivity(moveIntent)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager_flashsale, parent, false)
        return DaftarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

}