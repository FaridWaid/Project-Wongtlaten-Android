package com.wongtlaten.application.core

// Untuk menampung identitas dari data product
data class TempCostOngkir (
    val origin : String,
    val destination : String,
    val weight : Int,
    val code_courier : String,
    val name_courier : String,
    val service_courier : String,
    val description_courier : String,
    val cost_courier : Long,
    val estimate : String
) : java.io.Serializable{
    constructor(): this("","", 0, "", "", "", "", 0, ""){

    }
}