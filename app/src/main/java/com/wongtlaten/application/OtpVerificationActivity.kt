package com.wongtlaten.application

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wongtlaten.application.core.Customers
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Otp
import com.wongtlaten.application.modules.pembeli.home.HomePembeliActivity
import com.wongtlaten.application.modules.penjual.home.HomePenjualActivity
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.properties.Delegates

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var inputOTP1: EditText
    private lateinit var inputOTP2: EditText
    private lateinit var inputOTP3: EditText
    private lateinit var inputOTP4: EditText
    private lateinit var inputOTP5: EditText
    private lateinit var inputOTP6: EditText
    private lateinit var layoutResend: LinearLayout
    private lateinit var btnVerification: Button
    private lateinit var inputOtp : String
    private lateinit var refOtp : DatabaseReference
    private lateinit var ref : DatabaseReference
    private lateinit var timeLeft: TextView
    private lateinit var resendOtp: AppCompatImageView
    private var isResend by Delegates.notNull<Boolean>()
    private lateinit var identifyUser: String
    private var hours by Delegates.notNull<Long>()
    private var minutes by Delegates.notNull<Long>()
    private var seconds by Delegates.notNull<Long>()
    private var timeInMilies by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser
        refOtp = FirebaseDatabase.getInstance().getReference("OTP").child("${userIdentity?.uid}")
        ref = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child("${userIdentity?.uid}")

        hours = 0
        minutes = 0
        seconds = 0
        timeInMilies = 0
        layoutResend = findViewById(R.id.layoutResendOTP)
        resendOtp = findViewById(R.id.resendOtp)
        timeLeft = findViewById(R.id.timeLeft)
        timeLeft.visibility = View.VISIBLE
        startTimer()

        layoutResend.visibility = View.INVISIBLE
        isResend = false
        identifyUser = intent.getStringExtra(USER)!!

        resendOtp.setOnClickListener {
            if (isResend){
                loadingBar(2000)
                val email = intent.getStringExtra(EMAIL)!!
                refOtp.removeValue().addOnCompleteListener {
                    if (it.isSuccessful){
                        sendOtp(email)
                        isResend = false
                        layoutResend.visibility = View.INVISIBLE
                        timeLeft.visibility = View.VISIBLE
                        startTimer()
                    }
                }
            } else{
                alertDialog("Kode OTP Gagal Dikirimkan!", "Waktu tunggu untuk resend OTP belum selesai!", false)
            }
        }

        alertDialog("Kode OTP Berhasil Dikirimkan!", "Silakan cek email anda untuk mendapatkan kode OTP!", false)

        btnVerification = findViewById(R.id.btnVerifikasi)

        btnVerification.setOnClickListener {
            cekOtp()
        }

        init()
        addTextChangeListener()

    }

    private fun cekOtp() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usersOtp = dataSnapshot.getValue(Otp::class.java)
                val validOtp = usersOtp?.otp
                val otp1 = inputOTP1.text.toString().trim()
                val otp2 = inputOTP2.text.toString().trim()
                val otp3 = inputOTP3.text.toString().trim()
                val otp4 = inputOTP4.text.toString().trim()
                val otp5 = inputOTP5.text.toString().trim()
                val otp6 = inputOTP6.text.toString().trim()
                inputOtp = "$otp1$otp2$otp3$otp4$otp5$otp6"

                if (inputOtp == validOtp){
                    loadingBar(2000)
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val users = snapshot.getValue(Customers::class.java)!!
                            if (users.accessLevel == "customers"){
                                // Jika berhasil maka akan pindah activity ke activity OtpVerificationActivity
                                Intent(this@OtpVerificationActivity, HomePembeliActivity::class.java).also { intent ->
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
                                    finish()
                                }
                            }
                            else {
                                // Jika berhasil maka akan pindah activity ke activity OtpVerificationActivity
                                Intent(this@OtpVerificationActivity, HomePenjualActivity::class.java).also { intent ->
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            alertDialog("Gagal Login Ke Akun!", "error!", false)
                        }

                    })
                } else{
                    loadingBar(2000)
                    alertDialog("Verifikasi OTP Tidak Berhasil!", "Silakan cek kembali kode OTP anda pada email yang terhubung dengan akun untuk melakukan verifikasi kode OTP!", false)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        refOtp.addListenerForSingleValueEvent(menuListener)
    }

    private fun sendOtp(emailReceiver: String){

        //random OTP
        val rnd = Random()
        val kodeOtp = rnd.nextInt(999999)
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

            // Membuat variabel "newUser" yang berisikan beberapa data dan data tersebut diinputkan ke dalam Users
            val newOtp = Otp(identifyUser!!, emailReceiver, "$kodeOtp")
            // Jika idUser tidak null/kosong
            if (identifyUser != null){
                // Membuat suatu child realtime database baru dengan child = "idUser",
                // dan valuenya berisi data yang ada di dalam "newUser"
                refOtp.setValue(newOtp).addOnCompleteListener {
                    if (it.isSuccessful){
                        alertDialog("Kode OTP Berhasil Dikirimkan!", "Silakan cek email anda untuk mendapatkan kode OTP!", false)
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

    private fun addTextChangeListener() {
        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
    }

    private fun init() {
        inputOTP1 = findViewById(R.id.otpEditText1)
        inputOTP2 = findViewById(R.id.otpEditText2)
        inputOTP3 = findViewById(R.id.otpEditText3)
        inputOTP4 = findViewById(R.id.otpEditText4)
        inputOTP5 = findViewById(R.id.otpEditText5)
        inputOTP6 = findViewById(R.id.otpEditText6)
    }


    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) inputOTP2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) inputOTP5.requestFocus()

            }
        }

    }

    //membuat fungsi startTimer
    private fun startTimer() {
        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMilies = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timeLeft.visibility = View.INVISIBLE
                layoutResend.visibility = View.VISIBLE
                isResend = true
            }
        }.start()
    }

    //membuat fungsi updateCountDownText untuk menghitung waktu dari CountDownTimer dari value timeInMilies
    private fun updateCountDownText() {
        hours = (timeInMilies / 1000) as Long / 3600
        minutes = ((timeInMilies / 1000) as Long / 60) - (hours*60)
        seconds = (timeInMilies / 1000) as Long % 60
        val timeLeftFormatted = java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes.toInt(), seconds)
        timeLeft.setText("Sisa waktu pengiriman ulang OTP: $timeLeftFormatted")
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

    companion object {
        const val EMAIL = "EMAIL"
        const val USER = "USER"
    }

}