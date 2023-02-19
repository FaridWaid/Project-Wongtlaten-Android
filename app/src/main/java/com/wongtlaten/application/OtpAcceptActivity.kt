package com.wongtlaten.application

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wongtlaten.application.core.Utils
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class OtpAcceptActivity : AppCompatActivity() {

    private lateinit var email : EditText
    private lateinit var subject : EditText
    private lateinit var message : EditText
    private lateinit var button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_accept)

        email = findViewById(R.id.Etemail)
        subject = findViewById(R.id.Etsubject)
        message = findViewById(R.id.Etmessage)
        button = findViewById(R.id.btnSend)

        val mEmail = email.text.toString()
        val mSubject = subject.text.toString()
        val mMessage = message.text.toString()

//        val mail = SendMail(
//            "wongtlaten01@gmail.com", "wongtlaten123@",
//            "catatankomik@gmail.com",
//            "Testing Email",
//            "Yes, it's testing."
//        )

        button.setOnClickListener {

            val username = "wongtlaten01@gmail.com"
            val password = "xkktjnzfflmgsxsh"
            val props = Properties()
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })
            val mimeMessage = MimeMessage(session)
            try {
                mimeMessage.setFrom(InternetAddress(username))
                mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress("catatankomik@gmail.com").toString())
                mimeMessage.subject = "testing"
                mimeMessage.setText("testing email")
                Transport.send(mimeMessage)
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            } catch (e: MessagingException) {
                e.printStackTrace()
                Toast.makeText(getApplicationContext(), "${e}", Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "${mEmail}", Toast.LENGTH_LONG).show();
            }
        }

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

    }

//    private fun sendEmail(email: String, subject: String, message: String) {
//
//        val javaMailAPI = JavaMailAPI(this, email, subject, message)
//        javaMailAPI.execute()
//    }

}