package com.wongtlaten.application.modules.pembeli.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.*
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.penjual.home.KomentarProdukPenjualAdapter

class ReviewProdukPembeliAdapter(val list: ArrayList<com.wongtlaten.application.core.Transaction>, val listId: ArrayList<String>): RecyclerView.Adapter<ReviewProdukPembeliAdapter.DaftarViewHolder>() {

    var photoProduk: String = ""
    var nameProduct: String = ""
    var idReview: String = ""
    var totalRating: Float = 0.0F
    var countRating: Int = 0
    var checkUpdate: Boolean = false
    var reviewUpdate: ReviewProduct? = null
    var referenceDataProdukNormal: DatabaseReference = FirebaseDatabase.getInstance().reference
    var tempNewTransactionProduct: ArrayList<ProductTransaction> = arrayListOf()

    // Membuat class DaftarViewHolder yang digunakan untuk set view yang akan ditampilkan,
    // Menggunakan picasso untuk loading image
    inner class DaftarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageProduk: RoundedImageView = itemView.findViewById(R.id.imageProduk)
        val typeProduk: TextView = itemView.findViewById(R.id.typeProduk)
        val namaProduk: TextView = itemView.findViewById(R.id.nameProduk)
        fun bind(idProduct: String){
            with(itemView){
                referenceDataProdukNormal = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
                val menuListener2 = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val product = dataSnapshot.getValue(Products::class.java)!!
                        Picasso.get().load(product.photoProduct1).into(imageProduk)
                        namaProduk.text = product.namaProduct
                        typeProduk.text = product.kategoriProduct
                        photoProduk = product.photoProduct1
                        nameProduct = product.namaProduct
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
                    }
                }
                referenceDataProdukNormal.addListenerForSingleValueEvent(menuListener2)
                itemView.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_review_produk)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    var btnKirim = dialog.findViewById(R.id.btnKirim) as Button
                    var imageProduk = dialog.findViewById(R.id.imageProduk) as RoundedImageView
                    var nameProduk = dialog.findViewById(R.id.nameProduk) as TextView
                    var ratingBar = dialog.findViewById(R.id.ratingBar) as RatingBar
                    var etKomentar = dialog.findViewById(R.id.etKomentar) as TextInputEditText

                    var referenceReview: DatabaseReference = FirebaseDatabase.getInstance().getReference("dataReviewProduk").child(idProduct)
                    referenceReview.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val countChild = snapshot.childrenCount.toInt()
                            if (countChild == 0){
                                idReview = "REV00001"
                            } else {
                                totalRating = 0.0F
                                for (tempRating in snapshot.children){
                                    val review = tempRating.getValue(ReviewProduct::class.java)
                                    if (review != null){
                                        totalRating += review.rating
                                    }
                                }
                                countRating = snapshot.childrenCount.toInt()
                                val lastChild = referenceReview.limitToLast(1)
                                lastChild.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var lastIdReview = snapshot.getValue().toString()
                                        var newIdReview = lastIdReview.substring(4, 9).toLong()
                                        newIdReview += 1
                                        if (newIdReview < 10){
                                            idReview = "REV0000${newIdReview}"
                                        } else if (newIdReview < 100){
                                            idReview = "REV000${newIdReview}"
                                        } else if (newIdReview < 1000){
                                            idReview = "REV00${newIdReview}"
                                        } else if (newIdReview < 10000){
                                            idReview = "REV0${newIdReview}"
                                        } else if (newIdReview < 100000){
                                            idReview = "REV${newIdReview}"
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                    referenceDataProdukNormal = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
                    val menuListener3 = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val product = dataSnapshot.getValue(Products::class.java)!!
                            nameProduk.text = product.namaProduct
                            Picasso.get().load(product.photoProduct1).into(imageProduk)
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    referenceDataProdukNormal.addListenerForSingleValueEvent(menuListener3)

                    btnClose.setOnClickListener {
                        dialog.dismiss()
                    }
                    btnKirim.setOnClickListener {
                        checkUpdate = false
                        for (j in 0..list.size - 1){
                            tempNewTransactionProduct.clear()
                            for (k in 0..list[j].produkTransaction.size - 1){
                                if (list[j].produkTransaction[k].idProduk == idProduct && k != list[j].produkTransaction.size - 1){
                                    val newTempTransaction = ProductTransaction(list[j].produkTransaction[k].idProduk, list[j].produkTransaction[k].hargaProduk, list[j].produkTransaction[k].beratProduk, list[j].produkTransaction[k].totalBeli, "done")
                                    tempNewTransactionProduct.add(newTempTransaction)
                                    checkUpdate = true
                                } else if(list[j].produkTransaction[k].idProduk != idProduct && k != list[j].produkTransaction.size - 1){
                                    val newTempTransaction = ProductTransaction(list[j].produkTransaction[k].idProduk, list[j].produkTransaction[k].hargaProduk, list[j].produkTransaction[k].beratProduk, list[j].produkTransaction[k].totalBeli, list[j].produkTransaction[k].statusReview)
                                    tempNewTransactionProduct.add(newTempTransaction)
                                    checkUpdate = false
                                } else if (list[j].produkTransaction[k].idProduk == idProduct && k == list[j].produkTransaction.size - 1){
                                    reviewUpdate = ReviewProduct(list[j].idUser, idProduct, idReview, ratingBar.rating, etKomentar.text.toString())
                                    val newTempTransaction = ProductTransaction(list[j].produkTransaction[k].idProduk, list[j].produkTransaction[k].hargaProduk, list[j].produkTransaction[k].beratProduk, list[j].produkTransaction[k].totalBeli, "done")
                                    tempNewTransactionProduct.add(newTempTransaction)
                                    referenceReview.child(idReview).setValue(reviewUpdate).addOnCompleteListener {
                                        if (it.isSuccessful){
                                            val updateTransaction = Transaction(list[j].idUser, list[j].idTransaksi, list[j].jenisTransaksi, list[j].namePenerima, list[j].kotaTujuan, list[j].kodePos, list[j].alamatLengkap, list[j].teleponPenerima, list[j].totalBerat, list[j].jumlahOngkir, list[j].totalPembayaran, list[j].typePembayaran, list[j].waktuTransaksi, list[j].waktuPengiriman, list[j].statusPembayaran, list[j].statusProduk, list[j].kurir, list[j].resiPengiriman, list[j].catatanGiftcard, list[j].pdfUrl, tempNewTransactionProduct)
                                            val referenceTransaksi = FirebaseDatabase.getInstance().getReference("dataTransaksi").child(list[j].idTransaksi)
                                            referenceTransaksi.setValue(updateTransaction).addOnCompleteListener {
                                                if (it.isSuccessful){
                                                    checkUpdate = true
                                                    var totalAllRating = totalRating / countRating
                                                    val referenceProduk = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
                                                    val menuListener = object : ValueEventListener {
                                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                            val produk = dataSnapshot.getValue(Products::class.java)!!
                                                            val produkUpdate = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, totalAllRating,  produk.jumlahPembelianProduct, produk.statusProduct)
                                                            referenceProduk.setValue(produkUpdate).addOnCompleteListener {
                                                                dialog.dismiss()
                                                                Toast.makeText(context, "Review berhasil dikirimkan! Terima kasih.", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                        override fun onCancelled(databaseError: DatabaseError) {
                                                            // handle error
                                                        }
                                                    }
                                                    referenceProduk.addListenerForSingleValueEvent(menuListener)
                                                } else{
                                                    Toast.makeText(context, "${it.exception.toString()}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                    return@setOnClickListener
                                } else if(list[j].produkTransaction[k].idProduk != idProduct && k == list[j].produkTransaction.size - 1){
                                    reviewUpdate = ReviewProduct(list[j].idUser, idProduct, idReview, ratingBar.rating, etKomentar.text.toString())
                                    val newTempTransaction = ProductTransaction(list[j].produkTransaction[k].idProduk, list[j].produkTransaction[k].hargaProduk, list[j].produkTransaction[k].beratProduk, list[j].produkTransaction[k].totalBeli, list[j].produkTransaction[k].statusReview)
                                    tempNewTransactionProduct.add(newTempTransaction)
                                    if (checkUpdate){
                                        referenceReview.child(idReview).setValue(reviewUpdate).addOnCompleteListener {
                                            if (it.isSuccessful){
                                                val updateTransaction = Transaction(list[j].idUser, list[j].idTransaksi, list[j].jenisTransaksi, list[j].namePenerima, list[j].kotaTujuan, list[j].kodePos, list[j].alamatLengkap, list[j].teleponPenerima, list[j].totalBerat, list[j].jumlahOngkir, list[j].totalPembayaran, list[j].typePembayaran, list[j].waktuTransaksi, list[j].waktuPengiriman, list[j].statusPembayaran, list[j].statusProduk, list[j].kurir, list[j].resiPengiriman, list[j].catatanGiftcard, list[j].pdfUrl, tempNewTransactionProduct)
                                                val referenceTransaksi = FirebaseDatabase.getInstance().getReference("dataTransaksi").child(list[j].idTransaksi)
                                                referenceTransaksi.setValue(updateTransaction).addOnCompleteListener {
                                                    if (it.isSuccessful){
                                                        checkUpdate = true
                                                        var totalAllRating = totalRating / countRating
                                                        val referenceProduk = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
                                                        val menuListener = object : ValueEventListener {
                                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                                val produk = dataSnapshot.getValue(Products::class.java)!!
                                                                val produkUpdate = Products(produk.idProduct, produk.namaProduct, produk.hargaProduct, produk.stockProduct, produk.minimumPemesananProduct, produk.beratProduct, produk.kategoriProduct, produk.deskripsiProduct, produk.jenisProduct, produk.hargaPromoProduct, produk.photoProduct1, produk.photoProduct2, produk.photoProduct3, produk.photoProduct4, totalAllRating,  produk.jumlahPembelianProduct, produk.statusProduct)
                                                                referenceProduk.setValue(produkUpdate).addOnCompleteListener {
                                                                    dialog.dismiss()
                                                                    Toast.makeText(context, "Review berhasil dikirimkan! Terima kasih.", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                            override fun onCancelled(databaseError: DatabaseError) {
                                                                // handle error
                                                            }
                                                        }
                                                        referenceProduk.addListenerForSingleValueEvent(menuListener)
                                                    }
                                                }
                                            }
                                        }
                                        return@setOnClickListener
                                    }
                                    dialog.dismiss()
                                }

                            }
                        }
                    }

                    dialog.show()

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_produk, parent, false)
        return DaftarViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        holder.bind(listId[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = listId.size

}