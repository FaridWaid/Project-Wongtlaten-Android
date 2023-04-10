package com.wongtlaten.application.modules.penjual.home

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Transaction
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarTransaksiPenjualAdapter (private var list: ArrayList<Transaction>): RecyclerView.Adapter<DaftarTransaksiPenjualAdapter.DaftarTransaksiViewHolder>() {

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

    // Membuat class DaftarProdukViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarTransaksiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
        val idTransaksi: TextView = itemView.findViewById(R.id.idTransaksi)
        val namaPemesan: TextView = itemView.findViewById(R.id.namaPemesan)
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
                } else if (transaction.statusPembayaran == "pending"){
                    statusPembayaranLunas.visibility = View.INVISIBLE
                    statusPembayaranBelumLunas.visibility = View.VISIBLE
                    statusPembayaranBelumLunas.text = "Belum Lunas"
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
                idTransaksi.text = list[0].idTransaksi
                var referenceUser = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(list[0].idUser)
                val menuListener3 = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val users = dataSnapshot.getValue(Users::class.java)!!
                        namaPemesan.text = users.username
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
                    }
                }
                referenceUser.addListenerForSingleValueEvent(menuListener3)
                itemView.setOnClickListener {
                    tempTransaction.add(transaction)
                    onItemClickCallback?.onItemClicked(tempTransaction)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarTransaksiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaksi_penjual, parent, false)
        return DaftarTransaksiViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarTransaksiViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<Transaction>)
    }

    //Fungsi ini digunakan untuk memasukkan data ke dalam list
    fun filterList(filteredNames: ArrayList<Transaction>) {
        this.list = filteredNames
        notifyDataSetChanged()
    }
}