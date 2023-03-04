package com.wongtlaten.application

import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.properties.Delegates

class OtpVerificationTest {

    private var timeInMilies by Delegates.notNull<Long>()
    private lateinit var timeLeft: String

    @Test
    fun updateCountDownText() {
        timeInMilies = 20000
        timeLeft = ""
        val hours = (timeInMilies / 1000) as Long / 3600
        val minutes = ((timeInMilies / 1000) as Long / 60) - (hours*60)
        val seconds = (timeInMilies / 1000) as Long % 60
        val timeLeftFormatted = java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes.toInt(), seconds)
        timeLeft = "Sisa waktu pengiriman ulang OTP: $timeLeftFormatted"
        Assert.assertEquals("Sisa waktu pengiriman ulang OTP: 02.00", "$timeLeft")
    }

    @Test
    fun randomCode(){
        val rnd = Random()
        val kodeKategori = rnd.nextInt(999)
        while (kodeKategori.toString().length < 3){
            val kodeKategori = rnd.nextInt(999999)
        }

        val newKategori = "CAT$kodeKategori"
        Assert.assertEquals("$kodeKategori", newKategori)
    }
}