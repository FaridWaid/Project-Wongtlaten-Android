package com.wongtlaten.application.modules.pembeli.customize

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.modules.pembeli.wishlist.DaftarKeranjangProdukAdapter
import java.text.DecimalFormat
import java.text.NumberFormat

class CustomizeProdukPembeliAdapter(private var items: ArrayList<CustomizeProducts>) : RecyclerView.Adapter<CustomizeProdukPembeliAdapter.DaftarKategoriViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var tempProduct: ArrayList<CustomizeProducts> = arrayListOf()
    private var idProduct: ArrayList<String> = arrayListOf()
    private var tempHashProduct: HashMap<String, Int> = hashMapOf()
    private var hashTemp: HashMap<String, Int> = hashMapOf()
    private var totalProduk: HashMap<String, HashMap<String, Int>> = hashMapOf()
    private var countProdukBesar = 0
    private var countProdukSedang = 0
    private var countProdukKecil = 0

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarKategoriViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nonActive: View = itemView.findViewById(R.id.nonActive)
        val namaProduk: TextView = itemView.findViewById(R.id.namaProdukCustomize)
        val deskripsiProduk: TextView = itemView.findViewById(R.id.deskripsiProduk)
        val hargaProduk: TextView = itemView.findViewById(R.id.hargaProduk)
        val stokProduk: TextView = itemView.findViewById(R.id.stok)
        val imageProduk: ImageView = itemView.findViewById(R.id.imageProduk)
        val btnMinus: AppCompatImageView = itemView.findViewById(R.id.btnMinus)
        val btnPlus: AppCompatImageView = itemView.findViewById(R.id.btnPlus)
        val countProduk: TextView = itemView.findViewById(R.id.countProduk)
        val btnTambah: Button = itemView.findViewById(R.id.btnTambah)
        fun bind(product: CustomizeProducts){
            with(itemView){
                Picasso.get().load(product.photoProduct1).into(imageProduk)
                namaProduk.text = product.namaProduct
                deskripsiProduk.text = product.deskripsiProduct
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(product.hargaProduct)
                hargaProduk.text = "Rp. $formattedNumberPrice"
                stokProduk.text = "${product.stockProduct}"
                var countTotalProduk = 0
                if (product.stockProduct == 0){
                    nonActive.visibility = View.VISIBLE
                    btnTambah.setOnClickListener {
                        return@setOnClickListener
                    }
                } else {
                    btnTambah.setOnClickListener {
                        btnTambah.visibility = View.INVISIBLE
                        btnMinus.visibility = View.VISIBLE
                        btnPlus.visibility = View.VISIBLE
                        countProduk.visibility = View.VISIBLE
                        countTotalProduk = 1
                        countProduk.text = "$countTotalProduk"
                        tempProduct.add(product)
                        if (product.kategoriProduct == "besar"){
                            countProdukBesar += 1
                            hashTemp.put("besar", countProdukBesar)
                            if (product.idProduct !in tempHashProduct){
                                tempHashProduct.put(product.idProduct, 1)
                            } else{
                                var newCount = tempHashProduct.getValue(product.idProduct) + 1
                                tempHashProduct.put(product.idProduct, newCount)
                            }
                            totalProduk.put("besar", tempHashProduct)
                        } else if (product.kategoriProduct == "sedang"){
                            countProdukSedang += 1
                            hashTemp.put("sedang", countProdukSedang)
                            if (product.idProduct !in tempHashProduct){
                                tempHashProduct.put(product.idProduct, 1)
                            } else{
                                var newCount = tempHashProduct.getValue(product.idProduct) + 1
                                tempHashProduct.put(product.idProduct, newCount)
                            }
                            totalProduk.put("sedang", tempHashProduct)
                        } else{
                            countProdukKecil += 1
                            hashTemp.put("kecil", countProdukKecil)
                            if (product.idProduct !in tempHashProduct){
                                tempHashProduct.put(product.idProduct, 1)
                            } else{
                                var newCount = tempHashProduct.getValue(product.idProduct) + 1
                                tempHashProduct.put(product.idProduct, newCount)
                            }
                            totalProduk.put("kecil", tempHashProduct)
                        }
                        onItemClickCallback?.onItemClicked(tempProduct, hashTemp, totalProduk)
                    }
                    btnPlus.setOnClickListener {
                        if (countTotalProduk == product.stockProduct){
                            Toast.makeText(context, "Jumlah stock produk ini hanya $countTotalProduk stok", Toast.LENGTH_SHORT).show()
                        } else{
                            countTotalProduk += 1
                            countProduk.text = "$countTotalProduk"
                            tempProduct.add(product)
                            if (product.kategoriProduct == "besar"){
                                countProdukBesar += 1
                                hashTemp.put("besar", countProdukBesar)
                                var newCount = tempHashProduct.getValue(product.idProduct) + 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("besar", tempHashProduct)
                            } else if (product.kategoriProduct == "sedang"){
                                countProdukSedang += 1
                                hashTemp.put("sedang", countProdukSedang)
                                var newCount = tempHashProduct.getValue(product.idProduct) + 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("sedang", tempHashProduct)
                            } else{
                                countProdukKecil += 1
                                hashTemp.put("kecil", countProdukKecil)
                                var newCount = tempHashProduct.getValue(product.idProduct) + 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("kecil", tempHashProduct)
                            }
                            onItemClickCallback?.onItemClicked(tempProduct, hashTemp, totalProduk)
                        }
                    }
                    btnMinus.setOnClickListener {
                        if (countTotalProduk == 1){
                            btnTambah.visibility = View.VISIBLE
                            btnMinus.visibility = View.INVISIBLE
                            btnPlus.visibility = View.INVISIBLE
                            countProduk.visibility = View.INVISIBLE
                            tempProduct.remove(product)
                            if (product.kategoriProduct == "besar"){
                                countProdukBesar -= 1
                                hashTemp.put("besar", countProdukBesar)
                                if (tempHashProduct.getValue(product.idProduct) == 1){
                                    tempHashProduct.remove(product.idProduct)
                                }
                                totalProduk.put("besar", tempHashProduct)
                            } else if (product.kategoriProduct == "sedang"){
                                countProdukSedang -= 1
                                hashTemp.put("sedang", countProdukSedang)
                                if (tempHashProduct.getValue(product.idProduct) == 1){
                                    tempHashProduct.remove(product.idProduct)
                                }
                                totalProduk.put("sedang", tempHashProduct)
                            } else{
                                countProdukKecil -= 1
                                hashTemp.put("kecil", countProdukKecil)
                                var newCount = tempHashProduct.getValue(product.idProduct) - 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("kecil", tempHashProduct)
                            }
                            onItemClickCallback?.onItemClicked(tempProduct, hashTemp, totalProduk)
                        } else {
                            countTotalProduk -= 1
                            tempProduct.remove(product)
                            if (product.kategoriProduct == "besar"){
                                countProdukBesar -= 1
                                hashTemp.put("besar", countProdukBesar)
                                var newCount = tempHashProduct.getValue(product.idProduct) - 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("besar", tempHashProduct)
                            } else if (product.kategoriProduct == "sedang"){
                                countProdukSedang -= 1
                                hashTemp.put("sedang", countProdukSedang)
                                var newCount = tempHashProduct.getValue(product.idProduct) - 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("sedang", tempHashProduct)
                            } else{
                                countProdukKecil -= 1
                                hashTemp.put("kecil", countProdukKecil)
                                var newCount = tempHashProduct.getValue(product.idProduct) - 1
                                tempHashProduct.put(product.idProduct, newCount)
                                totalProduk.put("kecil", tempHashProduct)
                            }
                            onItemClickCallback?.onItemClicked(tempProduct, hashTemp, totalProduk)
                        }
                        countProduk.text = "$countTotalProduk"
                    }
                }
                itemView.setOnClickListener {
                    var dialog = Dialog(context)
                    dialog.requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.dialog_detail_produk_kustomisasi)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                    dialog.window!!.setGravity(Gravity.BOTTOM)

                    val btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
                    val imageKustomisasi = dialog.findViewById(R.id.imageKustomisasi) as ImageView
                    val namaProdukk = dialog.findViewById(R.id.textProduk) as TextView
                    val deskripsiProduk = dialog.findViewById(R.id.textDeskripsiProduk) as TextView
                    val hargaProduk = dialog.findViewById(R.id.textHargaProduk) as TextView
                    val beratProduk = dialog.findViewById(R.id.beratProduk) as TextView
                    val stokProduk = dialog.findViewById(R.id.stokProduk) as TextView
                    val kategoriProduk = dialog.findViewById(R.id.textKategori) as TextView

                    Picasso.get().load(product.photoProduct1).into(imageKustomisasi)
                    namaProdukk.text = product.namaProduct
                    deskripsiProduk.text = product.deskripsiProduct
                    beratProduk.text = "${product.beratProduct} gram"
                    stokProduk.text = product.stockProduct.toString()
                    kategoriProduk.text = product.kategoriProduct
                    val formatter: NumberFormat = DecimalFormat("#,###")
                    val price = product.hargaProduct
                    val formattedNumberPrice: String = formatter.format(price)
                    hargaProduk.text = "Rp. ${formattedNumberPrice}"


                    btnClose.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarKategoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_customize, parent, false)
        return DaftarKategoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaftarKategoriViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // Mendapatkan jumlah data dari list
    override fun getItemCount(): Int = items.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<CustomizeProducts>, hashTemp:HashMap<String, Int>, totalProduk:HashMap<String, HashMap<String, Int>>)
    }

}