package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.modules.pembeli.profile.DetailFaqPembeliActivity.Companion.EXTRA_ID_BANTUAN

class FaqPembeliAdapter(private var list: ArrayList<Faq>): RecyclerView.Adapter<FaqPembeliAdapter.DaftarFaqViewHolder>() {

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarFaqViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textPertanyaan: TextView = itemView.findViewById(R.id.textPertanyaan)
        val nextOptions: AppCompatImageView = itemView.findViewById(R.id.nextOptions)
        val nextButton: AppCompatImageView = itemView.findViewById(R.id.nextButton)
        fun bind(faq: Faq){
            nextOptions.visibility = View.INVISIBLE
            nextButton.visibility = View.VISIBLE
            with(itemView){
                textPertanyaan.text = faq.pertanyaan
                itemView.setOnClickListener {
                    val moveIntent = Intent(itemView.context, DetailFaqPembeliActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_BANTUAN, faq.idFaq)
                    itemView.context.startActivity(moveIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarFaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_bantuan, parent, false)
        return DaftarFaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarFaqViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    //Fungsi ini digunakan untuk memasukkan data ke dalam list
    fun filterList(filteredNames: ArrayList<Faq>) {
        this.list = filteredNames
        notifyDataSetChanged()
    }
}