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
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CategoryProduct
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity.Companion.EXTRA_ID_PRODUCT
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarProdukPenjualAdapter (private var list: ArrayList<Products>, val method: DaftarProdukPenjualActivity): RecyclerView.Adapter<DaftarProdukPenjualAdapter.DaftarProdukViewHolder>() {

    // Membuat class DaftarProdukViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarProdukViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val nameProduk: TextView = itemView.findViewById(R.id.nameProduk)
        val hargaProduk: TextView = itemView.findViewById(R.id.price)
        val promoProduk: TextView = itemView.findViewById(R.id.pricePromo)
        val stockProduk: TextView = itemView.findViewById(R.id.stockProduk)
        val optionProduk: AppCompatImageView = itemView.findViewById(R.id.optionButton)
        val btnUbahStock: Button = itemView.findViewById(R.id.btnUbahStock)
        val btnUbahHarga: Button = itemView.findViewById(R.id.btnUbahHarga)
        val cardProduk: CardView = itemView.findViewById(R.id.cardDaftarProduk)
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
                stockProduk.text = produk.stockProduct.toString()
                optionProduk.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_option_produk)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    val textPreviewProduk = dialog.findViewById(R.id.textPreviewProduk) as TextView
                    val textDeleteProduk = dialog.findViewById(R.id.textDeleteProduk) as TextView

                    textPreviewProduk.setOnClickListener {
                        val moveIntent = Intent(itemView.context, PreviewProdukActivity::class.java)
                        moveIntent.putExtra(EXTRA_ID_PRODUCT, produk.idProduct)
                        itemView.context.startActivity(moveIntent)
                        method.animationToTop()
                        dialog.dismiss()
                    }

                    textDeleteProduk.setOnClickListener {
                        val reference = FirebaseDatabase.getInstance().getReference("dataProduk").child("${produk.idProduct}")
                        reference.removeValue().addOnCompleteListener {
                            for (i in 0..3){
                                var ref = FirebaseStorage.getInstance().reference.child("imgProduct/${produk.idProduct}/$i")
                                if (i == 3){
                                    ref.delete().addOnCompleteListener {
                                        if (it.isSuccessful){
                                            dialog.dismiss()
                                            Toast.makeText(context, "Produk tersebut berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            dialog.dismiss()
                                            Toast.makeText(context, "Produk tersebut gagal dihapus!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    ref.delete()
                                }
                            }
                        }
                    }

                    btnClose.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }

                btnUbahStock.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_ubah_stok)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    val etStok = dialog.findViewById(R.id.etStok) as EditText
                    val btnSimpan = dialog.findViewById(R.id.btnSimpan) as Button

                    btnSimpan.setOnClickListener {
                        val stokInput = etStok.text.toString().trim()

                        if (stokInput.isEmpty()){
                            etStok.error = "Masukkan jumlah stok produk terlebih dahulu!"
                            etStok.requestFocus()
                            return@setOnClickListener
                        } else {
                            val productUpdate = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, stokInput.toInt(), produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, produk.ratingProduct,  produk.jumlahPembelianProduct)
                            val reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(produk.idProduct)
                            reference.setValue(productUpdate).addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(context, "Stok produk berhasil di ubah!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(context, "Stok produk gagal di ubah!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        }
                    }

                    btnClose.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }

                btnUbahHarga.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_ubah_harga)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    val etHarga = dialog.findViewById(R.id.etHarga) as EditText
                    val btnSimpan = dialog.findViewById(R.id.btnSimpan) as Button

                    btnSimpan.setOnClickListener {
                        val hargaInput = etHarga.text.toString().trim()

                        if (hargaInput.isEmpty()){
                            etHarga.error = "Masukkan harga produk terlebih dahulu!"
                            etHarga.requestFocus()
                            return@setOnClickListener
                        } else if (produk.jenisProduct == "flash sale"){
                            val productUpdate = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, hargaInput.toLong(), produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, produk.ratingProduct,  produk.jumlahPembelianProduct)
                            val reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(produk.idProduct)
                            reference.setValue(productUpdate).addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(context, "Harga promo produk berhasil di ubah!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(context, "Harga promo produk gagal di ubah!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        } else {
                            val productUpdate = Products(produk.idProduct, produk.namaProduct, hargaInput.toLong(), produk.stockProduct, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, produk.ratingProduct,  produk.jumlahPembelianProduct)
                            val reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(produk.idProduct)
                            reference.setValue(productUpdate).addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(context, "Harga produk berhasil di ubah!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(context, "Harga produk gagal di ubah!", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        }
                    }

                    btnClose.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }

                cardProduk.setOnClickListener {
                    val moveIntent = Intent(itemView.context, UbahProdukPenjualActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_PRODUCT, produk.idProduct)
                    itemView.context.startActivity(moveIntent)
                    method.animationToTop()
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarProdukViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_produk, parent, false)
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