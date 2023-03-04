package com.wongtlaten.application.modules.penjual.home

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CategoryListener
import com.wongtlaten.application.core.CategoryProduct

class DaftarKategoriProdukAdapter(private var list: ArrayList<CategoryProduct>, private var arrayList: ArrayList<String>, private var categoryListener: CategoryListener): RecyclerView.Adapter<DaftarKategoriProdukAdapter.DaftarKategoriViewHolder>() {

    var selectedList: ArrayList<String> = ArrayList()

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarKategoriViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textCategory: TextView = itemView.findViewById(R.id.kategori)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val deleteKategori: AppCompatImageView = itemView.findViewById(R.id.deleteKategori)

        fun bind(kategori: CategoryProduct){
            with(itemView){
                textCategory.text = kategori.namaCategory.toUpperCase()
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        arrayList.add(kategori.namaCategory)
                    } else {
                        arrayList.remove(kategori.namaCategory)
                    }
                    categoryListener.onQuantityChange(arrayList)
                }
                deleteKategori.setOnClickListener {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.apply {
                        setTitle("Konfirmasi")
                        setMessage("Yakin hapus kategori ${kategori.namaCategory}?")
                        setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                            val reference = FirebaseDatabase.getInstance().getReference("dataKategoriProduk").child("${kategori.idCategory}")
                            reference.removeValue().addOnCompleteListener {
                                if (it.isSuccessful){
                                    Toast.makeText(context, "Kategori tersebut berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Kategori tersebut gagal dihapus!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                    alertDialog.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarKategoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_kategori, parent, false)
        return DaftarKategoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarKategoriViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun getSelected(): ArrayList<String> {
        return selectedList
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = list.size

    //Fungsi ini digunakan untuk memasukkan data ke dalam list
    fun filterList(filteredNames: ArrayList<CategoryProduct>) {
        this.list = filteredNames
        notifyDataSetChanged()
    }


}