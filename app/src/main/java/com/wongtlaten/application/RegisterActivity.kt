package com.wongtlaten.application

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.wongtlaten.application.core.AttemptLogin
import com.wongtlaten.application.core.Customers
import com.wongtlaten.application.core.LoadingDialog
import java.util.regex.Pattern
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var refAttempt : DatabaseReference
    private lateinit var imageUri: Uri
    // Mendefinisikan variabel global dari view
    private lateinit var textLogin: TextView
    private lateinit var etUsername: TextInputEditText
    private lateinit var usernameContainer: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var emailContainer: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var passwordContainer: TextInputLayout
    private lateinit var btnRegistrasi: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        textLogin = findViewById(R.id.textLogin)
        etUsername = findViewById(R.id.etUsername)
        usernameContainer = findViewById(R.id.usernameContainer)
        etEmail = findViewById(R.id.etEmail)
        emailContainer = findViewById(R.id.emailContainer)
        etPassword = findViewById(R.id.etPassword)
        passwordContainer = findViewById(R.id.passwordContainer)
        btnRegistrasi = findViewById(R.id.btnRegistrasi)
        checkClick = true

        textLogin.setOnClickListener {
            // Pindah ke LoginActivity
            Intent(applicationContext, LoginActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                val loading = LoadingDialog(this@RegisterActivity)
                loading.isDissmis()
                finish()
            }
        }

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat database baru dengan reference users dan dimasukkan ke dalam variabel ref
        ref = FirebaseDatabase.getInstance().getReference("dataAkunCustomer")
        refAttempt = FirebaseDatabase.getInstance().getReference("attemptLogin")

        // Memanggil fungsi "usernameFocusListener", "emailFocusListener", "passwordFocusListener"
        usernameFocusListener()
        emailFocusListener()
        passwordFocusListener()

        // Ketika "btnRegistrasi" di klik maka akan mencoba mendaftarkan akun baru
        btnRegistrasi.setOnClickListener {

            if (checkClick) {
                checkClick = false

                // Membuat variabel baru yang berisi inputan user
                val username = etUsername.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
                emailContainer.helperText = validEmail()
                passwordContainer.helperText = validPassword()
                usernameContainer.helperText = validUsername()

                // Jika sudah benar, maka helper pada edittext diisikan dengan null
                val validEmail = emailContainer.helperText == null
                val validPassword = passwordContainer.helperText == null
                val validUsername = usernameContainer.helperText == null

                // Jika semua sudah diisi maka akan melakukan "createNewUser"
                if (validEmail && validPassword && validUsername) {
                    // Memanggil fungsi "createNewUser" dengan membawa variabel ("username","email","password"),
                    // Fungsi ini digunakan untuk membuat user baru
                    createNewUser(username, email, password)
                    loadingBar(6000)
                }else {
                    loadingBar(1000)
                    alertDialog("Gagal membuat akun!", "Pastikan anda menginputkan nama, email, dan password dengan benar!", false)
                    checkClick = true
                }
            } else {
                return@setOnClickListener
            }


        }

    }

    // Membuat fungsi "createNewUser"
    fun createNewUser(username: String, email: String, password: String) {
        // Membuat user baru dengan email dan password dan langsung tersambung ke Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    // Membuat variabel "idUser" yang berisikan id dari user baru yang telah berhasil dibuat
                    val idCustomers = auth.currentUser?.uid
                    // Membuat variabel refImage yang dihungkan dengan firebase storage
                    // variabel refImage ini digunakan untuk menyimpan foto imageUri dimasukkan,
                    // ke dalam firebase storage
                    imageUri = Uri.parse("android.resource://com.wongtlaten.application/drawable/acrylic")
                    val refImage = FirebaseStorage.getInstance().reference.child("imgProfilCustomer/${idCustomers}")
                    refImage.putFile(imageUri).addOnSuccessListener {
                        var downloadUrl: Uri? = null
                        refImage.downloadUrl.addOnSuccessListener { it1 ->
                            downloadUrl = it1
                            // Mengambil token untuk dimasukkan ke dalam user
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                                OnCompleteListener { task ->
                                    // Mendapatkan token baru
                                    val token = task.result
                                    // Membuat variabel "newUser" yang berisikan beberapa data dan data tersebut diinputkan ke dalam Users
                                    val newUser = Customers(idCustomers!!, username, "", "", email, downloadUrl.toString(), "", 0, "customers", token!!, "active", "inactive")
                                    // Jika idUser tidak null/kosong
                                    if (idCustomers != null){
                                        // Membuat suatu child realtime database baru dengan child = "idUser",
                                        // dan valuenya berisi data yang ada di dalam "newUser"
                                        ref.child(idCustomers).setValue(newUser).addOnCompleteListener {
                                            // Jika berhasil menambahkan child baru ke realtime database, maka akan memunculkan toast,
                                            val newAttempt = AttemptLogin(email, 0)
                                            refAttempt.child("$idCustomers").setValue(newAttempt).addOnCompleteListener {
                                                if (it.isSuccessful){
                                                    val userIdentity = auth.currentUser!!
                                                    userIdentity.sendEmailVerification().addOnCompleteListener {
                                                        if (it.isSuccessful){
                                                            // Kemudian pindah activity ke activity LoginActivity
                                                            Intent(this@RegisterActivity, LoginActivity::class.java).also { intent ->
                                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                startActivity(intent)
                                                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                                                                val loading = LoadingDialog(this@RegisterActivity)
                                                                loading.isDissmis()
                                                                finish()
                                                            }
                                                            Toast.makeText(this,"Silakan buka email anda untuk memverifikasi akun yang anda buat!",Toast.LENGTH_SHORT).show()
                                                            checkClick = true
                                                        } else {
                                                            alertDialog("Gagal Memverifikasi Akun!", "Pastikan anda menginputkan email dengan benar!", false)
                                                            checkClick = true
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // Jika gagal menambahkan child baru ke realtime database, maka akan memunculkan toast gagal
                                        alertDialog("Gagal membuat akun!", "Pastikan anda menginputkan nama, email, dan password dengan benar!", false)
                                        checkClick = true
                                    }
                                })
                        }
                    }
                } else{
                    // Jika gagal membuat akun baru, maka akan memunculkan toast error
                    alertDialog("Gagal membuat akun!", "Email yang anda inputkan sudah terdaftar!", false)
                    checkClick = true
                }
            }
    }

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
    private fun alertDialog(title: String, message: String, backActivity: Boolean){
        // Membuat variabel yang berisikan AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle(title)
            setMessage(message)
            window.setBackgroundDrawableResource(android.R.color.background_light)
            setCancelable(false)
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (backActivity){
                        onBackPressed()
                    }
                })
        }
        alertDialog.show()
    }

    // Membuat fungsi "usernameFocusListener"
    private fun usernameFocusListener() {
        // Memastikan apakah etUsername sudah sesuai dengan format pengisian
        etUsername.setOnFocusChangeListener { _, focused ->
            if(!focused){
                usernameContainer.helperText = validUsername()
            }
        }
    }

    // Membuat fungsi "validUsername"
    private fun validUsername(): String? {
        val username = etUsername.text.toString()
        // Jika username kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (username.isEmpty()){
            return "Username Harus Diisi!"
        }
        return null
    }

    // Membuat fungsi "emailFocusListener"
    private fun emailFocusListener() {
        // Memastikan apakah etEmail sudah sesuai dengan format pengisian
        etEmail.setOnFocusChangeListener { _, focused ->
            if(!focused){
                emailContainer.helperText = validEmail()
            }
        }
    }

    // Membuat fungsi "validEmail"
    private fun validEmail(): String? {

        val EMAIL_ADDRESS_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(gmail)+\\.(com)\$")

        val email = etEmail.text.toString()
        // Jika email kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (email.isEmpty()){
            return "Email Harus Diisi!"
        }
        // Jika email tidak sesuai format maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email Tidak Valid. Seharusnya your@gmail.com"
        }
        // Jika email tidak sesuai format maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if(!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            return "Email Tidak Valid. Seharusnya your@gmail.com"
        }
        return null
    }

    // Membuat fungsi "passwordFocusListener"
    private fun passwordFocusListener() {
        // Memastikan apakah etPassword sudah sesuai dengan format pengisian
        etPassword.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                passwordContainer.helperText = validPassword()
            }
        }
    }

    // Membuat fungsi "validPassword"
    private fun validPassword(): String? {
        val password = etPassword.text.toString()
        // Jika password kosong maka akan gagal login dan muncul error harus isi terlebih dahulu
        if (password.isEmpty()){
            return "Password Harus Diisi!"
        }
        // Jika panjang password kurang dari 6 maka akan gagal login
        if(password.length < 6) {
            return "Password Harus Lebih Dari 6 Karakter!"
        }
        return null
    }

    // Membuat fungsi "loadingBar" dengan parameter time,
    // Fungsi ini digunakan untuk menampilkan loading dialog
    private fun loadingBar(time: Long) {
        val loading = LoadingDialog(this)
        loading.startDialog()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                loading.isDissmis()
            }

        }, time)
    }

}