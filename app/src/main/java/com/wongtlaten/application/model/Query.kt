package com.wongtlaten.application.model

data class Query(
    val courier: String,
    val destination: String,
    val origin: String,
    val weight: Int
)