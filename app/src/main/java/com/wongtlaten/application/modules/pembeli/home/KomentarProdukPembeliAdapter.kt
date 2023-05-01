package com.wongtlaten.application.modules.pembeli.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.ReviewProduct
import com.wongtlaten.application.core.Users
import de.hdodenhof.circleimageview.CircleImageView

class KomentarProdukPembeliAdapter(private var list: ArrayList<ReviewProduct>): RecyclerView.Adapter<KomentarProdukPembeliAdapter.DaftarKomentarViewHolder>() {

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarKomentarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProfile: CircleImageView = itemView.findViewById(R.id.ivProfile)
        val namaPembeli: TextView = itemView.findViewById(R.id.namaPembeli)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val textKomentar: TextView = itemView.findViewById(R.id.textKomentar)
        fun bind(review: ReviewProduct){
            with(itemView){
                val referenceUser = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(review.idUser)
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val users = dataSnapshot.getValue(Users::class.java)!!
                        Picasso.get().load(users.photoProfil).into(imageProfile)
                        namaPembeli.text = users.username
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
                    }
                }
                referenceUser.addListenerForSingleValueEvent(menuListener)
                ratingBar.rating = review.rating
                textKomentar.text = review.review
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarKomentarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_komentar, parent, false)
        return DaftarKomentarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarKomentarViewHolder, position: Int) {
        holder.bind(list[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

}