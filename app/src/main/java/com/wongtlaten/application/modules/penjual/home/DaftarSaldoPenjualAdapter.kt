package com.wongtlaten.application.modules.penjual.home

import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.penjual.payment.DetailPesananPenjualActivity
import com.wongtlaten.application.modules.penjual.payment.DetailPesananPenjualActivity.Companion.EXTRA_TRANSACTION
import java.text.DecimalFormat
import java.text.NumberFormat

class DaftarSaldoPenjualAdapter(private var list: ArrayList<Transaction>): RecyclerView.Adapter<DaftarSaldoPenjualAdapter.DaftarSaldoViewHolder>() {

    val tempTransaction: ArrayList<Transaction> = arrayListOf()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarSaldoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textTanggal: TextView = itemView.findViewById(R.id.textTanggal)
        val textNamaProduk: TextView = itemView.findViewById(R.id.textNamaProduk)
        val textTotal: TextView = itemView.findViewById(R.id.textTotal)
        fun bind(transaction: Transaction){
            with(itemView){
                textTanggal.text = transaction.waktuTransaksi
                textNamaProduk.text = transaction.jenisTransaksi
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(transaction.totalPembayaran)
                textTotal.text = "+ Rp. $formattedNumberPrice"
                itemView.setOnClickListener {
                    tempTransaction.add(transaction)
                    onItemClickCallback?.onItemClicked(tempTransaction)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarSaldoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_saldo, parent, false)
        return DaftarSaldoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarSaldoViewHolder, position: Int) {
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