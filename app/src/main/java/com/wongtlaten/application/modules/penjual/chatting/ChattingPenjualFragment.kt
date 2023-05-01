package com.wongtlaten.application.modules.penjual.chatting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.penjual.home.DaftarAkunPenjualAdapter
import de.hdodenhof.circleimageview.CircleImageView

class ChattingPenjualFragment : Fragment() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    private lateinit var referenceUser : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarUsers: RecyclerView
    private lateinit var daftarUsersList: ArrayList<Users>
    private lateinit var adapter: ChattingPenjualAdapter
    private lateinit var etSearch: EditText
    private lateinit var photoProfil: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatting_penjual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!

        photoProfil = view.findViewById(R.id.ivProfile)

        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(userIdentity.uid)
        referenceUser = FirebaseDatabase.getInstance().getReference("dataAkunUser")

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarUsers = view.findViewById(R.id.rvDaftarChatting)
        rvDaftarUsers.setHasFixedSize(true)
        rvDaftarUsers.layoutManager = LinearLayoutManager(context)

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarUsersList = arrayListOf<Users>()

        adapter = ChattingPenjualAdapter(daftarUsersList)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListUsers()

        // Memanggil fungsi keepData
        keepData()

        // Mendefinisikan variabel "etSearch", ketika memasukkan query ke etSearch maka akan memanggil fungsi filter
        // terdapat closeSearch digunakan untuk menghapus query/inputan
        // overridePendingTransition digunakan untuk animasi dari intent
        etSearch = view.findViewById(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
            }

        })

    }

    private fun showListUsers() {
        referenceUser.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarUsersList.clear()
                    for (i in snapshot.children){
                        val users = i.getValue(Users::class.java)
                        if (users != null && users.accessLevel == "customers"){
                            daftarUsersList.add(users)
                        }
                    }

                    adapter = ChattingPenjualAdapter(daftarUsersList)
                    rvDaftarUsers.adapter = adapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun keepData() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                Picasso.get().load(users.photoProfil).into(photoProfil)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
    }

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    private fun filter(text: String) {
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<Users>()
        // setiap data yang ada pada daftarProdukList disamakan dengan filteredNames
        daftarUsersList.filterTo(filteredNames) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.username.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredNames)
    }

}