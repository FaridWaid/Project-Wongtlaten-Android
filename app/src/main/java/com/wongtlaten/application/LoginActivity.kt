package com.wongtlaten.application

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.wongtlaten.application.core.AttemptLogin
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Otp
import com.wongtlaten.application.modules.pembeli.home.HomePembeliActivity
import com.wongtlaten.application.modules.penjual.home.HomePenjualActivity
import java.util.*
import java.util.regex.Pattern
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var ref : DatabaseReference
    private lateinit var refOtp : DatabaseReference
    private lateinit var refAttempt : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var textRegister: TextView
    private lateinit var lupaPassword: AppCompatImageView
    private lateinit var btnLogin: Button
    private lateinit var etEmail: TextInputEditText
    private lateinit var emailContainer: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var passwordContainer: TextInputLayout
    private lateinit var identifyUser : String
    private lateinit var changeEmail : String
    private var attemptLogin by Delegates.notNull<Int>()
    private var cekAttempt by Delegates.notNull<Int>()
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        textRegister = findViewById(R.id.textRegister)
        lupaPassword = findViewById(R.id.nextButton)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmail)
        emailContainer = findViewById(R.id.emailContainer)
        etPassword = findViewById(R.id.etPassword)
        passwordContainer = findViewById(R.id.passwordContainer)
        identifyUser = ""
        attemptLogin = 0
        cekAttempt = 0
        checkClick = true

        try {
            changeEmail = intent.getStringExtra(CHANGE_EMAIL)!!
        } catch (e: Exception){
            changeEmail = "false"
        }

        if (changeEmail == "true"){
            alertDialog("Konfirmasi!", "Email anda berhasil diubah, silakan verifikasi email baru anda terlebih dahulu!", false)
        }

        textRegister.setOnClickListener {
            // Pindah ke RegisterActivity
            Intent(applicationContext, RegisterActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//                val loading = LoadingDialog(this@LoginActivity)
//                loading.isDissmis()
                finish()
            }
        }

        lupaPassword.setOnClickListener {
            // Pindah ke ForgotPasswordActivity
            Intent(applicationContext, ForgotPasswordActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat database baru dengan reference users dan dimasukkan ke dalam variabel ref
        ref = FirebaseDatabase.getInstance().getReference("dataAkunCustomer")
        refOtp = FirebaseDatabase.getInstance().getReference("OTP")
        refAttempt = FirebaseDatabase.getInstance().getReference("attemptLogin")



        btnLogin.setOnClickListener {

            if (checkClick){
                checkClick = false
                // Membuat variabel baru yang berisi inputan user
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
                emailContainer.helperText = validEmail()
                passwordContainer.helperText = validPassword()

                // Jika sudah benar, maka helper pada edittext diisikan dengan null
                val validEmail = emailContainer.helperText == null
                val validPassword = passwordContainer.helperText == null

                // Jika semua sudah diisi maka akan melakukan "loginUser"
                if (validEmail && validPassword) {
                    loadingBar(6000)
                    // Memanggil fungsi "loginUser" dengan membawa variabel ("email","password"),
                    // Fungsi ini digunakan untuk masuk ke halaman user
                    loginUser(email, password)
                }else{
                    loadingBar(1000)
                    alertDialog("Gagal Login Ke Akun!", "Pastikan email dan password yang anda inputkan sudah benar!", false)
                    // Jika gagal maka akan memunculkan toast gagal
                    checkClick = true
                }
            } else {
                return@setOnClickListener
            }


        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Memanggil fungsi "passwordFocusListener", "emailFocusListener"
        emailFocusListener()
        passwordFocusListener()

    }

     fun loginUser(email: String, password: String) {
        // Masuk ke halaman user dengan email dan password sebagai autentikasi dan langsung tersambung ke Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    val userIdentity = auth.currentUser!!
                    if (userIdentity.isEmailVerified){
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                            // mendapatkan token baru
                            val token = task.result
                            // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
                            val reference = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child("${auth.currentUser?.uid}")
                            // Mengambil token untuk dimasukkan ke dalam user
                            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val users = snapshot.getValue(Users::class.java)!!
                                    val userUpdate = Users(users.idUsers!!, users.username, users.kelamin, users.alamat, users.email, users.photoProfil, users.noTelp, users.jumlahTransaksi, users.accessLevel, token!!, users.status, users.checkOtp)
                                    reference.setValue(userUpdate).addOnCompleteListener {
                                        if (it.isSuccessful){

                                            if (users.checkOtp == "active"){
                                                checkClick = true
                                                identifyUser = users.idUsers
                                                sendOtp(users.email)
                                            }
                                            else if (users.accessLevel == "customers"){
                                                checkClick = true
                                                // Jika berhasil maka akan pindah activity ke activity HomePembeliActivity
                                                Intent(this@LoginActivity, HomePembeliActivity::class.java).also { intent ->
                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(intent)
                                                    overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
                                                    val loading = LoadingDialog(this@LoginActivity)
                                                    loading.isDissmis()
                                                    finish()
                                                }
                                            }
                                            else {
                                                checkClick = true
                                                // Jika berhasil maka akan pindah activity ke activity HomePenjualActivity
                                                Intent(this@LoginActivity, HomePenjualActivity::class.java).also { intent ->
                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(intent)
                                                    overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
                                                    val loading = LoadingDialog(this@LoginActivity)
                                                    loading.isDissmis()
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    checkClick = true
                                    alertDialog("Gagal Login Ke Akun!", "error!", false)
                                }

                            })

                        })
                    } else{
                        checkClick = true
                        alertDialog("Gagal Login Ke Akun!", "Verifikasi email anda terlebih dahulu, silakan cek email anda untuk memverifikasi akun!", false)
                    }
                } else{
                    ref.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                for (i in snapshot.children){
                                    val cekUsers = i.getValue(Users::class.java)
                                    if (cekUsers != null && cekUsers.email == email){
                                        refAttempt.child("${cekUsers.idUsers}").addListenerForSingleValueEvent(object : ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val attemptUser = snapshot.getValue(AttemptLogin::class.java)!!
                                                cekAttempt = attemptUser.attempt
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                            }

                                        })
                                        if (cekAttempt < 3){
                                            checkClick = true
                                            cekAttempt += 1
                                            val newAttempt = AttemptLogin(email, cekAttempt)
                                            refAttempt.child("${cekUsers.idUsers}").setValue(newAttempt)
                                        } else{
                                            checkClick = true
                                            // Pindah ke ResetPasswordActivity
                                            Intent(applicationContext, ResetPasswordActivity::class.java).also {
                                                it.putExtra("EMAIL", email)
                                                it.putExtra("USER", identifyUser)
                                                startActivity(it)
                                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                            }
                                        }
                                    } else{
                                        checkClick = true
                                        alertDialog("Gagal Login Ke Akun!", "Pastikan email dan password yang anda inputkan sudah benar!", false)
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            }
    }

     fun sendOtp(emailReceiver: String){

        //random OTP
        val rnd = Random()
        val kodeOtp = rnd.nextInt(999999)
        while (kodeOtp.toString().length < 6){
            val kodeOtp = rnd.nextInt(999999)
        }
        String.format("%06d", kodeOtp)

        val emailSender = "wongtlaten01@gmail.com"
        val passwordSender = "xkktjnzfflmgsxsh"
        var messageEmail = "$kodeOtp adalah kode verifikasi akun wongtlaten Anda."
        val props = Properties()
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        val session = Session.getDefaultInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(emailSender, passwordSender)
            }
        })
        val mimeMessage = MimeMessage(session)
        try {
            mimeMessage.setFrom(InternetAddress(emailSender))
            mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress("$emailReceiver").toString())
            mimeMessage.subject = "KODE OTP"
            mimeMessage.setText("$messageEmail")
            Transport.send(mimeMessage)

            // Membuat variabel "newOtp" yang berisikan beberapa data dan data tersebut diinputkan ke dalam Users
            val newOtp = Otp(identifyUser!!, emailReceiver, "$kodeOtp")
            // Jika idUser tidak null/kosong
            if (identifyUser != null){
                // Membuat suatu child realtime database baru dengan child = "idUser",
                // dan valuenya berisi data yang ada di dalam "newOtp"
                refOtp.child("$identifyUser").setValue(newOtp).addOnCompleteListener {
                    if (it.isSuccessful){
                        Intent(this@LoginActivity, OtpVerificationActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("EMAIL", emailReceiver)
                            intent.putExtra("USER", identifyUser)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                            val loading = LoadingDialog(this@LoginActivity)
                            loading.isDissmis()
                            finish()
                        }
                    }
                }
            }
        } catch (e: MessagingException) {
//            e.printStackTrace()
            alertDialog("Kode OTP Gagal Dikirimkan!", "${e}!", false)
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
     fun passwordFocusListener() {
        // Memastikan apakah etPassword sudah sesuai dengan format pengisian
        etPassword.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                passwordContainer.helperText = validPassword()
            }
        }
    }

    // Membuat fungsi "validPassword"
     fun validPassword(): String? {
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

    companion object{
        const val CHANGE_EMAIL = "CHANGE_EMAIL"
    }

}