package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.UbahProdukKustomisasiPenjualActivity.Companion.EXTRA_ID_PRODUCT
import java.text.DecimalFormat
import java.text.NumberFormat

class KustomisasiProdukPenjualAdapter (private var list: ArrayList<CustomizeProducts>, val method: KustomisasiProdukPenjualActivity): RecyclerView.Adapter<KustomisasiProdukPenjualAdapter.DaftarProdukViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


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
        fun bind(produk: CustomizeProducts){
            with(itemView){
                Picasso.get().load(produk.photoProduct1).into(imageProduk)
                nameProduk.text = produk.namaProduct
                val formatter: NumberFormat = DecimalFormat("#,###")
                val price = produk.hargaProduct
                val formattedNumberPrice: String = formatter.format(price)
                hargaProduk.text = "Rp. ${formattedNumberPrice}"
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
                        var dialog = Dialog(context)
                        dialog.requestWindowFeature(
                            Window.FEATURE_NO_TITLE
                        )
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_detail_produk_kustomisasi)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                        dialog.window!!.setGravity(Gravity.BOTTOM)

                        val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                        val imageKustomisasi = dialog.findViewById(R.id.imageKustomisasi) as ImageView
                        val namaProdukk = dialog.findViewById(R.id.textProduk) as TextView
                        val deskripsiProduk = dialog.findViewById(R.id.textDeskripsiProduk) as TextView
                        val hargaProduk = dialog.findViewById(R.id.textHargaProduk) as TextView
                        val beratProduk = dialog.findViewById(R.id.beratProduk) as TextView
                        val panjangProduk = dialog.findViewById(R.id.panjangProduk) as TextView
                        val lebarProduk = dialog.findViewById(R.id.lebarProduk) as TextView
                        val stokProduk = dialog.findViewById(R.id.stokProduk) as TextView
                        val kategoriProduk = dialog.findViewById(R.id.textKategori) as TextView

                        Picasso.get().load(produk.photoProduct1).into(imageKustomisasi)
                        namaProdukk.text = produk.namaProduct
                        deskripsiProduk.text = produk.deskripsiProduct
                        beratProduk.text = "${produk.beratProduct} gram"
                        panjangProduk.text = "${produk.panjangProduct} cm"
                        lebarProduk.text = "${produk.lebarProduct} cm"
                        stokProduk.text = produk.stockProduct.toString()
                        kategoriProduk.text = produk.kategoriProduct
                        val formatter: NumberFormat = DecimalFormat("#,###")
                        val price = produk.hargaProduct
                        val formattedNumberPrice: String = formatter.format(price)
                        hargaProduk.text = "Rp. ${formattedNumberPrice}"


                        btnClose.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                    textDeleteProduk.setOnClickListener {
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.apply {
                            setTitle("Konfirmasi")
                            setMessage("Apakah anda yakin ingin menghapus produk ${produk.namaProduct}?")
                            setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })
                            setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                                val reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child("${produk.idProduct}")
                                val productUpdate = CustomizeProducts(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct, produk.beratProduct, produk.panjangProduct, produk.lebarProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.photoProduct1, "deleted")
                                reference.setValue(productUpdate).addOnCompleteListener {
                                    if (it.isSuccessful){
                                        dialog.dismiss()
                                        onItemClickCallback?.onItemClicked()
                                        Toast.makeText(context, "Produk tersebut berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        dialog.dismiss()
                                        Toast.makeText(context, "Produk tersebut gagal dihapus!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                        alertDialog.show()
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
                            val productUpdate = CustomizeProducts(produk.idProduct, produk.namaProduct, produk.hargaProduct, stokInput.toInt(), produk.beratProduct, produk.panjangProduct, produk.lebarProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.photoProduct1, produk.statusProduct)
                            val reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(produk.idProduct)
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
                        } else {
                            val productUpdate = CustomizeProducts(produk.idProduct, produk.namaProduct, hargaInput.toLong(), produk.stockProduct, produk.beratProduct, produk.panjangProduct, produk.lebarProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.photoProduct1, produk.statusProduct)
                            val reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(produk.idProduct)
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
                    val moveIntent = Intent(itemView.context, UbahProdukKustomisasiPenjualActivity::class.java)
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
    fun filterList(filteredNames: ArrayList<CustomizeProducts>) {
        this.list = filteredNames
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked()
    }

}