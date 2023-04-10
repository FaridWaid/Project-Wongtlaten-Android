package com.wongtlaten.application.modules.pembeli.wishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.ProductTransaction
import com.wongtlaten.application.core.Transaction
import java.text.DecimalFormat
import java.text.NumberFormat

class DetailPesananPembeliAdapter(val list: ArrayList<Transaction>): RecyclerView.Adapter<DetailPesananPembeliAdapter.DaftarViewHolder>() {

    var listProduct: ArrayList<ProductTransaction> = list[0].produkTransaction
    var idProduk: String = ""
    var photoProduk: String = ""
    var namaProduct: String = ""
    var referenceDataProduk: DatabaseReference = FirebaseDatabase.getInstance().reference

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
        val totalPembayaran: TextView = itemView.findViewById(R.id.totalPembayaran)
        fun bind(transaction: ProductTransaction){
            with(itemView){

                idProduk = transaction.idProduk
                if (list[0].jenisTransaksi == "custom"){
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
                } else {
                    typeProduk.text = "Normal"
                    referenceDataProduk = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduk)
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
                }
                datePemesanan.text = list[0].waktuTransaksi
                if (list[0].statusPembayaran == "settlement"){
                    statusPembayaranLunas.visibility = View.VISIBLE
                    statusPembayaranBelumLunas.visibility = View.INVISIBLE
                } else if (list[0].statusPembayaran == "expire"){
                    statusPembayaranLunas.visibility = View.INVISIBLE
                    statusPembayaranBelumLunas.visibility = View.VISIBLE
                    statusPembayaranBelumLunas.text = "Cancel"
                } else{
                    statusPembayaranLunas.visibility = View.INVISIBLE
                    statusPembayaranBelumLunas.visibility = View.VISIBLE
                }
                statusTransaksi.text = list[0].statusProduk
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(transaction.hargaProduk * transaction.totalBeli)
                totalPembayaran.text = "Rp. $formattedNumberPrice"
                countProduk.text = "${transaction.totalBeli} produk"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_pembayaran, parent, false)
        return DaftarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        holder.bind(listProduct[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = listProduct.size
}