package com.wongtlaten.application.modules.penjual.home

import org.junit.Assert.*
import org.junit.Test
import java.util.*


internal class TambahKategoriProdukPenjualActivityTest {

    @Test
    fun randomCode(): String{
        val rnd = Random()
        val kodeKategori = rnd.nextInt(999)
        while (kodeKategori.toString().length < 3){
            val kodeKategori = rnd.nextInt(999999)
        }

        val newKategori = "CAT$kodeKategori"
        return newKategori
    }

    @Test
    fun testKali() {
        val angka1 = 4.0
        val angka2 = 10.0
        val result = angka1 * angka2
        val expResult = 20.0
        assertEquals(expResult, result, 0.0)
    }
}