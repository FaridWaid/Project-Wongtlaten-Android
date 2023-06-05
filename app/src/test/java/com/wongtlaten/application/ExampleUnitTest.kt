package com.wongtlaten.application

import android.view.View
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkIdCity() {
        var idOrigin = 0
        var idDestination = 0
        val dropDownKotaAsal = "Sidoarjo"
        val dropDownKotaTujuan = "Mojokerto"
        val daftarCityList: ArrayList<String> = arrayListOf("Jakarta Selatan", "Jakarta Timut", "Jakarta Barat", "Jakarta Utara", "Bandung", "Sidoarjo", "Mojokerto")
        for (i in 0..daftarCityList.size - 1){
            if (daftarCityList[i] == dropDownKotaAsal){
                idOrigin = i + 1
            }
            if (daftarCityList[i] == dropDownKotaTujuan){
                idDestination = i + 1
            }
        }
        assertEquals(6, idOrigin)
        assertEquals(7, idDestination)
    }

    @Test
    fun ruleBasedGiftbox() {
        var countProdukBesar = 2
        var countProdukSedang = 2
        var countProdukKecil = 2
        var checkGiftbox = 4
        var countTotalProduk = countProdukBesar + countProdukSedang + countProdukKecil
        if (countTotalProduk > 10){
            checkGiftbox = 0
        } else if (countTotalProduk == 0){
            checkGiftbox = 0
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 1 && countProdukKecil == 0){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 1 && countProdukKecil <= 4){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 1 && countProdukKecil <= 8){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 2 && countProdukKecil == 0){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 2 && countProdukKecil == 1){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 2 && countProdukKecil <= 7){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 3 && countProdukKecil == 0){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 3 && countProdukKecil <= 6){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 4 && countProdukKecil == 0){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang == 4 && countProdukKecil <= 5){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukSedang > 4){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukKecil <= 4){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukBesar == 1 && countProdukKecil <= 9){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 1){
            checkGiftbox = 3
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 1 && countProdukKecil <= 5){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 1 && countProdukKecil > 5){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 2 && countProdukKecil <= 3){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 2 && countProdukKecil > 3){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang == 2){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukSedang > 2){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 2 && countProdukKecil <= 8){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 2){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 1 && countProdukKecil <= 3){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 1 && countProdukKecil > 3){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 2 && countProdukKecil >= 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang == 2){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukSedang > 2){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukKecil <= 5){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 3 && countProdukKecil > 5){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 3){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang == 1 && countProdukKecil == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang == 1 && countProdukKecil > 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukSedang > 1){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukKecil <= 2){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukBesar == 4 && countProdukKecil > 2){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 4){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar == 5 && countProdukSedang >= 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 5 && countProdukKecil >= 1){
            checkGiftbox = 5
        } else if (countTotalProduk <= 10 && countProdukBesar == 5){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukBesar > 5){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil == 0){
            checkGiftbox = 1
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil <= 4){
            checkGiftbox = 1
        } else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil <= 7){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukSedang == 1 && countProdukKecil <= 9){
            checkGiftbox = 3
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil == 0){
            checkGiftbox = 1
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil == 1){
            checkGiftbox = 1
        } else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil <= 4){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil <= 7){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukSedang == 2 && countProdukKecil <= 8){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 3  && countProdukKecil == 0){
            checkGiftbox = 2
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 3 && countProdukKecil == 1){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukSedang == 3 && countProdukKecil <= 4){
            checkGiftbox = 3
        } else if (countTotalProduk <= 10 && countProdukSedang == 3 && countProdukKecil <= 7){
            checkGiftbox = 4
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 4  && countProdukKecil == 0){
            checkGiftbox = 3
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 4 && countProdukKecil <= 3){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukSedang == 4 && countProdukKecil > 3){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 5 && countProdukKecil == 0){
            checkGiftbox = 4
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 5 && countProdukKecil == 1){
            checkGiftbox = 4
        } else if (countTotalProduk <= 10 && countProdukSedang == 5 && countProdukKecil > 1){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang == 6 && countProdukKecil == 0){
            checkGiftbox = 4
        }
        else if (countTotalProduk <= 10 && countProdukSedang == 6 && countProdukKecil > 1){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukSedang > 6){
            checkGiftbox = 5
        }

        else if (countTotalProduk <= 10 && countProdukKecil <= 4){
            checkGiftbox = 1
        } else if (countTotalProduk <= 10 && countProdukKecil <= 7){
            checkGiftbox = 2
        } else if (countTotalProduk <= 10 && countProdukKecil <= 10){
            checkGiftbox = 3
        }

        assertEquals(4, checkGiftbox)
    }
}