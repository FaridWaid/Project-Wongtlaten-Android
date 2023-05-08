package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.customize.CustomizeProdukPembeliActivity
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliAdapter
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity
import com.wongtlaten.application.modules.penjual.home.UbahProdukPenjualActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.log

class RiwayatTransaksiPembeliAdapter(val list: ArrayList<Transaction>): RecyclerView.Adapter<RiwayatTransaksiPembeliAdapter.DaftarViewHolder>() {

    var idProduk: String = ""
    var photoProduk: String = ""
    var namaProduct: String = ""
    var referenceDataProduk: DatabaseReference = FirebaseDatabase.getInstance().reference
    var referenceDataProdukNormal: DatabaseReference = FirebaseDatabase.getInstance().reference
    val tempTransaction: ArrayList<Transaction> = arrayListOf()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
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
        val btnBeli: Button = itemView.findViewById(R.id.btnBeli)
        val btnReview: Button = itemView.findViewById(R.id.btnReview)
        fun bind(transaction: Transaction){
            with(itemView){

                idProduk = transaction.produkTransaction[0].idProduk
                if (transaction.jenisTransaksi == "custom"){
                    typeProduk.text = "Customize"
                    referenceDataProduk = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(idProduk)
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
                } else if (transaction.jenisTransaksi == "normal") {
                    typeProduk.text = "Normal"
                    referenceDataProdukNormal = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduk)
                    val menuListener2 = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val product = dataSnapshot.getValue(Products::class.java)!!
                            Picasso.get().load(product.photoProduct1).into(imageProduk)
                            namaProduk.text = product.namaProduct
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    referenceDataProdukNormal.addListenerForSingleValueEvent(menuListener2)
                }
                datePemesanan.text = transaction.waktuTransaksi
                if (transaction.statusPembayaran == "settlement"){
                    statusPembayaranLunas.visibility = View.VISIBLE
                    statusPembayaranBelumLunas.visibility = View.INVISIBLE
                } else{
                    statusPembayaranLunas.visibility = View.INVISIBLE
                    statusPembayaranBelumLunas.visibility = View.VISIBLE
                    statusPembayaranBelumLunas.text = "Cancel"
                }
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
                btnBeli.setOnClickListener {
                    tempTransaction.add(transaction)
                    onItemClickCallback?.btnBeliClicked(tempTransaction)
                }
                btnReview.setOnClickListener {
                    onItemClickCallback?.btnReviewClicked()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_transaksi, parent, false)
        return DaftarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<Transaction>)
        fun btnBeliClicked(data: ArrayList<Transaction>)
        fun btnReviewClicked()
    }
}