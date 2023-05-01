package com.wongtlaten.application.modules.penjual.chatting

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Chat
import com.wongtlaten.application.core.Users
import de.hdodenhof.circleimageview.CircleImageView

class ChatPenjualActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imgProfile: CircleImageView
    private lateinit var tvTextName: TextView
    private lateinit var btnSendMessage: ImageButton
    private lateinit var etMessage: EditText
    private lateinit var chatRecyclerView: RecyclerView
    var chatList = ArrayList<Chat>()
    private lateinit var idUsers: String
    private lateinit var idChats: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        imgProfile = findViewById(R.id.imgProfile)
        tvTextName = findViewById(R.id.tvUserName)
        btnSendMessage = findViewById(R.id.btnSendMessage)
        etMessage = findViewById(R.id.etMessage)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        idChats = ""

        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        idUsers = intent.getStringExtra(EXTRA_ID_USERS)!!

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        databaseReference = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(idUsers)
        reference = FirebaseDatabase.getInstance().getReference("dataChatting").child(idUsers)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)!!
                tvTextName.text = user.username
                Picasso.get().load(user.photoProfil).into(imgProfile)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        // Memanggil value/child terakhir dari database daftarpengumulan untuk mendifinisikan idProduk yang terbaru
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countChild = snapshot.childrenCount.toInt()
                if (countChild == 0){
                    idChats = "CHT00001"
                } else {
                    val lastChild = reference.limitToLast(1)
                    lastChild.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var lastIdChat = snapshot.getValue().toString()
                            var newIdChat = lastIdChat.substring(4, 9).toLong()
                            newIdChat += 1
                            if (newIdChat < 10){
                                idChats = "CHT0000${newIdChat}"
                            } else if (newIdChat < 100){
                                idChats = "CHT000${newIdChat}"
                            } else if (newIdChat < 1000){
                                idChats = "CHT00${newIdChat}"
                            } else if (newIdChat < 10000){
                                idChats = "CHT0${newIdChat}"
                            } else if (newIdChat < 100000){
                                idChats = "CHT${newIdChat}"
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

        btnSendMessage.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            var message: String = etMessage.text.toString()

            if (message.isEmpty()){
                Toast.makeText(applicationContext, "Tolong masukkan pesan", Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            } else {
                sendMessage(idChats, currentUser!!.uid, idUsers, message)
                etMessage.setText("")
            }
        }

        updateMessage(idUsers, currentUser!!.uid)
        readMessage(currentUser!!.uid, idUsers)

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun sendMessage(idChat: String, senderId: String, receiverId: String, message: String){

        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("idChat", idChat)
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)
        hashMap.put("statusMessage", "0")

        reference.child(idChat).setValue(hashMap)
    }

    fun readMessage(senderId: String, receiverId: String) {
        val databaseReferenceChat: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("dataChatting").child(idUsers)

        databaseReferenceChat.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)!!
                    if (chat.senderId.equals(senderId) && chat.receiverId.equals(receiverId) ||
                        chat.senderId.equals(receiverId) && chat.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatPenjualAdapter(this@ChatPenjualActivity, chatList)
                chatRecyclerView.adapter = chatAdapter
                chatRecyclerView.smoothScrollToPosition(chatList.size - 1)
            }
        })
    }

    fun updateMessage(senderId: String, receiverId: String) {
        val databaseReferenceChat: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("dataChatting").child(idUsers)

        databaseReferenceChat.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)!!
                    if (chat.senderId.equals(senderId) && chat.receiverId.equals(receiverId)
                    ) {
                        if (chat.statusMessage == "0"){
                            val updateMessage = Chat(chat.idChat, chat.senderId, chat.receiverId, chat.message, "1")
                            databaseReferenceChat.child(chat.idChat).setValue(updateMessage)
                        }
                    }
                }
            }
        })
    }

    companion object{
        const val EXTRA_ID_USERS = "EXTRA_ID_USERS"
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    // Fungsi ini digunakan untuk menampilkan dialog peringatan tidak tersambung ke internet,
    // jika tetep tidak connect ke internet maka tetap looping dialog tersebut
    private fun showInternetDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle("PERINGATAN!")
            setMessage("Tidak ada koneksi internet, mohon nyalakan mobile data/wifi anda terlebih dahulu")
            setIcon(R.drawable.ic_alert)
            setCancelable(false)
            setPositiveButton(
                "Coba lagi",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (!isConnected(this@ChatPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: ChatPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}