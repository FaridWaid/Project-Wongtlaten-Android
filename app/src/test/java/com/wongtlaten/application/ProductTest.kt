package com.wongtlaten.application

import android.view.View
import com.google.firebase.database.*
import com.wongtlaten.application.core.Customers
import com.wongtlaten.application.core.Products
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.properties.Delegates

class ProductTest {

    private lateinit var ref : DatabaseReference
    private lateinit var hmm : String
    private var checkQuantity by Delegates.notNull<Boolean>()
    private lateinit var daftarCheckedList: ArrayList<String>

    @Test
    fun showListProduk(){
        ref = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child("3PUrfP7SFyPMaA74dk3CVmGWsF82")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val customers = snapshot.getValue(Customers::class.java)!!
                val customerUpdate = Customers(customers.idCustomers!!, customers.username, customers.kelamin, customers.alamat, customers.email, customers.photoProfil, customers.noTelp, customers.jumlahTransaksi, customers.accessLevel, customers.token, customers.status, "active")
                ref.setValue(customerUpdate).addOnCompleteListener {
                    if (it.isSuccessful){
                        hmm = "yes"
                    } else {
                        hmm = "fuck"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        assertEquals("yes", hmm)
    }

    @Test
    fun onQuantityChange(){
        daftarCheckedList = arrayListOf("oke", "tambah")
        if (daftarCheckedList.isEmpty()){
            checkQuantity = false
        } else {
            checkQuantity = true
        }
        assertEquals(true, checkQuantity)
    }


}