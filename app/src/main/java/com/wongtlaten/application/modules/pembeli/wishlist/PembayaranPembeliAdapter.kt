package com.wongtlaten.application.modules.pembeli.wishlist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.*
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity.Companion.EXTRA_TRANSACTION
import java.text.DecimalFormat
import java.text.NumberFormat

class PembayaranPembeliAdapter(val list: ArrayList<Transaction>): RecyclerView.Adapter<PembayaranPembeliAdapter.DaftarViewHolder>() {

    var idProduct: String = ""
    var photoProduk: String = ""
    var namaProduct: String = ""
    var referenceDataProduk: DatabaseReference = FirebaseDatabase.getInstance().reference
    val tempTransaction: ArrayList<Transaction> = arrayListOf()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: PembayaranPembeliAdapter.OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val typeProduk: TextView = itemView.findViewById(R.id.typeProduk)
        val datePemesanan: TextView = itemView.findViewById(R.id.datePemesanan)
        val statusPembayaranBelumLunas: TextView = itemView.findViewById(R.id.statusPembayaranBelumLunas)
        val statusPembayaranLunas: TextView = itemView.findViewById(R.id.statusPembayaranLunas)
        val statusTransaksi: TextView = itemView.findViewById(R.id.statusTransaksi)
        val namaProduk: TextView = itemView.findViewById(R.id.nameProduk)
        val countProduk: TextView = itemView.findViewById(R.id.countProduk)
        val textProdukLainnya: TextView = itemView.findViewById(R.id.textProdukLainnya)
        val totalPembayaran: TextView = itemView.findViewById(R.id.totalPembayaran)
        fun bind(transaction: Transaction){
            with(itemView){

                idProduct = transaction.produkTransaction[0].idProduk
                if (transaction.jenisTransaksi == "custom"){
                    typeProduk.text = "Customize"
                    referenceDataProduk = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(idProduct)
                    val menuListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val product = dataSnapshot.getValue(CustomizeProducts::class.java)!!
                            Picasso.get().load(product.photoProduct1).into(imageProduk)
                            namaProduk.text = product.namaProduct
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    referenceDataProduk.addListenerForSingleValueEvent(menuListener)
                } else {
                    typeProduk.text = "Normal"
                    referenceDataProduk = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
                    val menuListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val product = dataSnapshot.getValue(Products::class.java)!!
                            Picasso.get().load(product.photoProduct1).into(imageProduk)
                            namaProduk.text = product.namaProduct
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    referenceDataProduk.addListenerForSingleValueEvent(menuListener)
                }
                datePemesanan.text = transaction.waktuTransaksi
                statusPembayaranLunas.visibility = View.INVISIBLE
                statusPembayaranBelumLunas.visibility = View.VISIBLE
                statusTransaksi.text = transaction.statusProduk
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(transaction.totalPembayaran)
                totalPembayaran.text = "Rp. $formattedNumberPrice"
                if (transaction.produkTransaction.size > 1){
                    textProdukLainnya.visibility = View.VISIBLE
                    textProdukLainnya.text = "+${transaction.produkTransaction.size - 1} produk lainnya"
                }
                countProduk.text = "${transaction.produkTransaction[0].totalBeli} produk"
                itemView.setOnClickListener {
                    tempTransaction.add(transaction)
                    onItemClickCallback?.onItemClicked(tempTransaction)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_pembayaran, parent, false)
        return DaftarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<Transaction>)
    }
}