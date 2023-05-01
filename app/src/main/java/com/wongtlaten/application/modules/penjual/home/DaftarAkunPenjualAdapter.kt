package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.penjual.home.DetailAkunPenjualActivity.Companion.EXTRA_ID_USERSS
import de.hdodenhof.circleimageview.CircleImageView

class DaftarAkunPenjualAdapter(private var list: ArrayList<Users>): RecyclerView.Adapter<DaftarAkunPenjualAdapter.DaftarAkunViewHolder>() {

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarAkunViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cardAkun: CardView = itemView.findViewById(R.id.cardDaftarAkun)
        val imageProfile: CircleImageView = itemView.findViewById(R.id.imageProfile)
        val namaPembeli: TextView = itemView.findViewById(R.id.namePembeli)
        val countPembelian: TextView = itemView.findViewById(R.id.countPembelian)
        val editOtp: AppCompatImageView = itemView.findViewById(R.id.editOtp)
        val editStatus: AppCompatImageView = itemView.findViewById(R.id.editStatus)
        fun bind(users: Users){
            with(itemView){
                Picasso.get().load(users.photoProfil).into(imageProfile)
                namaPembeli.text = users.username
                countPembelian.text = "Jumlah pembelian: ${users.jumlahTransaksi}"
                editOtp.setOnClickListener {
                    val moveIntent = Intent(itemView.context, UbahStatusOtpPembeliActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_USERSS, users.idUsers)
                    itemView.context.startActivity(moveIntent)
                }
                editStatus.setOnClickListener {
                    val moveIntent = Intent(itemView.context, UbahStatusAkunPembeliActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_USERSS, users.idUsers)
                    itemView.context.startActivity(moveIntent)
                }
                cardAkun.setOnClickListener {
                    val moveIntent = Intent(itemView.context, DetailAkunPenjualActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_USERSS, users.idUsers)
                    itemView.context.startActivity(moveIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarAkunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_akun, parent, false)
        return DaftarAkunViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarAkunViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    //Fungsi ini digunakan untuk memasukkan data ke dalam list
    fun filterList(filteredNames: ArrayList<Users>) {
        this.list = filteredNames
        notifyDataSetChanged()
    }
}