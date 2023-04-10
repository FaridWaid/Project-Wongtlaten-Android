package com.wongtlaten.application.modules.pembeli.customize

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.SectionCustomize


class MainCustomizeProdukPembeliAdapter(private var sectionList: List<SectionCustomize>): RecyclerView.Adapter<MainCustomizeProdukPembeliAdapter.ViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var tempProduct: ArrayList<CustomizeProducts> = arrayListOf()
    private var tempHashIdProduct: HashMap<String, Int> = hashMapOf()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sectionNameTextView: TextView = itemView.findViewById(R.id.ukuran);
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.childRecyclerView);
        fun bind(section: SectionCustomize){
            var sectionName = section.getSectionName()
            var sectionItems = section.getSectionItems()
            with(itemView){
                sectionNameTextView.text = sectionName
                val childRecyclerAdapter = CustomizeProdukPembeliAdapter(sectionItems)
                childRecyclerView.adapter = childRecyclerAdapter
                childRecyclerAdapter.setOnItemClickCallback(object : CustomizeProdukPembeliAdapter.OnItemClickCallback{
                    override fun onItemClicked(
                        data: ArrayList<CustomizeProducts>,
                        hashTemp: HashMap<String, Int>,
                        totalProduk: HashMap<String, HashMap<String, Int>>
                    ) {
                        tempProduct = data
                        if (!totalProduk.get("kecil").isNullOrEmpty()){
                            for ((key, value) in totalProduk.getValue("kecil")) {
                                tempHashIdProduct.put(key, value)
                            }
                        }
                        if (!totalProduk.get("sedang").isNullOrEmpty()){
                            for ((key, value) in totalProduk.getValue("sedang")) {
                                tempHashIdProduct.put(key, value)
                            }
                        }
                        if (!totalProduk.get("besar").isNullOrEmpty()){
                            for ((key, value) in totalProduk.getValue("besar")) {
                                tempHashIdProduct.put(key, value)
                            }
                        }

                        var newHashMap: HashMap<String, Int> = hashMapOf()
                        for ((key, value) in tempHashIdProduct) {
                            if (value != 0){
                                newHashMap.put(key, value)
                            }
                        }
                        onItemClickCallback?.onItemClicked(tempProduct, hashTemp, newHashMap)
                    }

                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.section_customize_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sectionList[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = sectionList.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<CustomizeProducts>, hashTemp:HashMap<String, Int>, totalProduk:HashMap<String, Int>)
    }
}