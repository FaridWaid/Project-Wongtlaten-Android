package com.wongtlaten.application.model

data class RajaongkirX(
    val destination_details: DestinationDetails,
    val origin_details: OriginDetails,
    val query: Query,
    val results: List<ResultX>,
    val status: StatusX
)