package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.LoginActivity
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Customers
import com.wongtlaten.application.core.LoadingDialog
import de.hdodenhof.circleimageview.CircleImageView

class ProfilePembeliFragment : Fragment() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var textName: TextView
    private lateinit var textTransaksi: TextView
    private lateinit var textTotal: TextView
    private lateinit var photoProfil: CircleImageView
    // Mendefinisikan variabel global dari view
    private lateinit var nextDataPribadi: AppCompatImageView
    private lateinit var nextKeamanan: AppCompatImageView
    private lateinit var nextLogout: AppCompatImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_pembeli, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!

        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        textName = view.findViewById(R.id.nameAccount)
        textTransaksi = view.findViewById(R.id.transaction)
        textTotal = view.findViewById(R.id.countTotal)
        photoProfil = view.findViewById(R.id.ivProfile)
        nextDataPribadi = view.findViewById(R.id.iconNextDataPribadi)
        nextKeamanan = view.findViewById(R.id.iconNextKeamanan)
        nextLogout = view.findViewById(R.id.iconNextLogout)

        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(userIdentity.uid)

        // Memanggil fungsi loadingBar dan mengeset time = 4000
        loadingBar(1000)

        // Memanggil fungsi keepData
        keepData()

        // Mendefinisikan variabel item fitur 2
        // overridePendingTransition digunakan untuk animasi dari intent
        nextDataPribadi.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarSampahAdminActivity
            requireActivity().run{
                startActivity(Intent(this, ProfileDataPribadiPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextKeamanan.setOnClickListener {
            // Jika berhasil maka akan pindah ke DaftarSampahAdminActivity
            requireActivity().run{
                startActivity(Intent(this, ProfileKeamananPembeliActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        nextLogout.setOnClickListener {
            auth.signOut()
            requireActivity().run{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }

    private fun keepData() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val customers = dataSnapshot.getValue(Customers::class.java)!!
                textName.text = customers.username
                textTransaksi.text = customers.jumlahTransaksi.toString()
                Picasso.get().load(customers.photoProfil).into(photoProfil)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
    }

    // Membuat fungsi "loadingBar" dengan parameter time,
    // Fungsi ini digunakan untuk menampilkan loading dialog
    private fun loadingBar(time: Long) {
        val loading = LoadingDialog(requireActivity())
        loading.startDialog()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                loading.isDissmis()
            }

        }, time)
    }

}