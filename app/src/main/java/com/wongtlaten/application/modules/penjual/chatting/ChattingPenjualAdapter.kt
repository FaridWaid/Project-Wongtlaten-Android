package com.wongtlaten.application.modules.penjual.chatting

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Chat
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.pembeli.profile.DetailFaqPembeliActivity
import com.wongtlaten.application.modules.penjual.chatting.ChatPenjualActivity.Companion.EXTRA_ID_USERS
import de.hdodenhof.circleimageview.CircleImageView

class ChattingPenjualAdapter(private var list: ArrayList<Users>): RecyclerView.Adapter<ChattingPenjualAdapter.DaftarAkunViewHolder>() {

    // Membuat class DaftarKategoriViewHolder yang digunakan untuk set view yang akan ditampilkan
    inner class DaftarAkunViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        val cardAkun: CardView = itemView.findViewById(R.id.cardDaftarAkun)
        val imageProfile: CircleImageView = itemView.findViewById(R.id.ivProfile)
        val namaPembeli: TextView = itemView.findViewById(R.id.namaPembeli)
        val textPesanBaruReguler: TextView = itemView.findViewById(R.id.textPesanBaruRegular)
        val textPesanBaruBold: TextView = itemView.findViewById(R.id.textPesanBaruBold)
        fun bind(users: Users){
            textPesanBaruReguler.visibility = View.VISIBLE
            textPesanBaruBold.visibility = View.INVISIBLE
            with(itemView){
                Picasso.get().load(users.photoProfil).into(imageProfile)
                namaPembeli.text = users.username

                val databaseReferenceChat: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("dataChatting").child(users.idUsers)

                databaseReferenceChat.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val auth = FirebaseAuth.getInstance()
                        val currentUser = auth.currentUser!!
                        for (i in snapshot.children) {
                            val chat = i.getValue(Chat::class.java)!!
                            if (chat.senderId.equals(users.idUsers) && chat.receiverId.equals(currentUser.uid)
                            ) {
                                if (chat.statusMessage == "0"){
                                    textPesanBaruBold.visibility = View.VISIBLE
                                    textPesanBaruReguler.visibility = View.INVISIBLE
                                } else{
                                    textPesanBaruReguler.visibility = View.VISIBLE
                                    textPesanBaruBold.visibility = View.INVISIBLE
                                    textPesanBaruReguler.text = "tulis pesan baru"
                                }
                            }
                        }
                    }
                })

                itemView.setOnClickListener {
                    val moveIntent = Intent(itemView.context, ChatPenjualActivity::class.java)
                    moveIntent.putExtra(EXTRA_ID_USERS, users.idUsers)
                    itemView.context.startActivity(moveIntent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarAkunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_chatting, parent, false)
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