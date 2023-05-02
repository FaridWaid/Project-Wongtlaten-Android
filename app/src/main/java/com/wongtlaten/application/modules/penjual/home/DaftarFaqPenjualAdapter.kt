package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.home.PreviewBantuanPenjualActivity.Companion.EXTRA_ID_BANTUAN
import com.wongtlaten.application.modules.penjual.home.UbahFaqPenjualActivity.Companion.EXTRA_ID_FAQ

class DaftarFaqPenjualAdapter(private var list: ArrayList<Faq>): RecyclerView.Adapter<DaftarFaqPenjualAdapter.DaftarFaqViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarFaqViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textPertanyaan: TextView = itemView.findViewById(R.id.textPertanyaan)
        val nextOptions: AppCompatImageView = itemView.findViewById(R.id.nextOptions)
        val nextButton: AppCompatImageView = itemView.findViewById(R.id.nextButton)
        fun bind(faq: Faq){
            nextOptions.visibility = View.VISIBLE
            nextButton.visibility = View.INVISIBLE
            with(itemView){
                textPertanyaan.text = faq.pertanyaan
                nextOptions.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_option_bantuan)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    val textPreviewBantuan = dialog.findViewById(R.id.textPreviewBantuan) as TextView
                    val textDeleteBantuan = dialog.findViewById(R.id.textDeleteBantuan) as TextView

                    textPreviewBantuan.setOnClickListener {
                        val moveIntent = Intent(itemView.context, PreviewBantuanPenjualActivity::class.java)
                        moveIntent.putExtra(EXTRA_ID_BANTUAN, faq.idFaq)
                        itemView.context.startActivity(moveIntent)
                        dialog.dismiss()
                    }

                    textDeleteBantuan.setOnClickListener {
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.apply {
                            setTitle("Konfirmasi")
                            setMessage("Apakah anda yakin ingin menghapus bantuan ${faq.pertanyaan}?")
                            setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })
                            setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                                val reference = FirebaseDatabase.getInstance().getReference("dataFaq").child("${faq.idFaq}")
                                reference.removeValue().addOnCompleteListener {
                                    if (it.isSuccessful){
                                        dialog.dismiss()
                                        Toast.makeText(context, "Bantuan tersebut berhasil dihapus!", Toast.LENGTH_SHORT).show()
                                        list.remove(faq)
                                        onItemClickCallback?.onItemClicked(list)
                                    } else {
                                        dialog.dismiss()
                                        Toast.makeText(context, "Bantuan tersebut gagal dihapus!", Toast.LENGTH_SHORT).show()
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

                itemView.setOnClickListener {
                    val moveIntent = Intent(itemView.context, UbahFaqPenjualActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_FAQ, faq.idFaq)
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

    interface OnItemClickCallback {
        fun onItemClicked(dataList: ArrayList<Faq>)
    }

}