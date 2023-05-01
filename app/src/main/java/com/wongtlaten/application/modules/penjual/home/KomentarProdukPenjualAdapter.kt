package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.ReviewProduct
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.pembeli.home.KomentarProdukPembeliAdapter
import de.hdodenhof.circleimageview.CircleImageView

class KomentarProdukPenjualAdapter(private var list: ArrayList<ReviewProduct>): RecyclerView.Adapter<KomentarProdukPenjualAdapter.DaftarKomentarViewHolder>() {

    var totalRating: Float = 0.0F
    var countRating: Int = 0
    var totalAllRating: Float = 0.0F
    var idProduct: String = ""
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarKomentarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProfile: CircleImageView = itemView.findViewById(R.id.ivProfile)
        val namaPembeli: TextView = itemView.findViewById(R.id.namaPembeli)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val textKomentar: TextView = itemView.findViewById(R.id.textKomentar)
        val optionButton: AppCompatImageView = itemView.findViewById(R.id.optionButton)
        fun bind(review: ReviewProduct){
            optionButton.visibility = View.VISIBLE
            with(itemView){
                idProduct = review.idProduk
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
                optionButton.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_option_review)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    val textDeleteReview = dialog.findViewById(R.id.textDeleteReview) as TextView


                    textDeleteReview.setOnClickListener {

                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.apply {
                            setTitle("Konfirmasi")
                            setMessage("Apakah anda yakin ingin menghapus review ini?")
                            setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })
                            setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()

                                val reference = FirebaseDatabase.getInstance().getReference("dataReviewProduk").child("${review.idProduk}")
                                reference.child("${review.idReview}").removeValue().addOnCompleteListener {
                                    if (it.isSuccessful){
                                        Toast.makeText(context, "Review tersebut berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                        var referenceReview: DatabaseReference = FirebaseDatabase.getInstance().getReference("dataReviewProduk").child(idProduct)
                                        referenceReview.addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                totalRating = 0.0F
                                                for (tempRating in snapshot.children){
                                                    val reviews = tempRating.getValue(ReviewProduct::class.java)
                                                    if (reviews != null){
                                                        totalRating += reviews.rating
                                                    }
                                                }
                                                countRating = snapshot.childrenCount.toInt()
                                                if (countRating == 0){
                                                    totalAllRating = 0.0F
                                                } else{
                                                    totalAllRating = totalRating / countRating
                                                }
                                                var referenceProduk = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
                                                referenceProduk.addValueEventListener(object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        val produk = snapshot.getValue(Products::class.java)!!
                                                        val produkUpdate = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, totalAllRating,  produk.jumlahPembelianProduct, produk.statusProduct)
                                                        referenceProduk.setValue(produkUpdate).addOnCompleteListener {
                                                            if (it.isSuccessful){
                                                                dialog.dismiss()
                                                                onItemClickCallback?.onItemClicked()
                                                            }
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {

                                                    }

                                                })
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }

                                        })
                                    } else {
                                        dialog.dismiss()
                                        Toast.makeText(context, "Review tersebut gagal dihapus!", Toast.LENGTH_SHORT).show()
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

    interface OnItemClickCallback {
        fun onItemClicked()
    }
}