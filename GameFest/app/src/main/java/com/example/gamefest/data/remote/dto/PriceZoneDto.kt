package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PriceZoneDto(
    val id: Int,
    @SerializedName("festival_id") val festivalId: Int,
    val name: String,
    @SerializedName("table_price") val tablePrice: Double,
    val tableTypes: List<TableTypeDto>? = null,
    val mapZones: List<MapZoneDto>? = null
)

data class TableTypeDto(
    val id: Int,
    val name: String,
    @SerializedName("nb_total") val nbTotal: Double,
    @SerializedName("nb_available") val nbAvailable: Double,
    @SerializedName("nb_total_player") val nbTotalPlayer: Int,
    @SerializedName("price_zone_id") val priceZoneId: Int
)

data class MapZoneDto(
    val id: Int,
    @SerializedName("festival_id") val festivalId: Int,
    @SerializedName("price_zone_id") val priceZoneId: Int?,
    val name: String,
    @SerializedName("small_tables") val smallTables: Int,
    @SerializedName("large_tables") val largeTables: Int,
    @SerializedName("city_tables") val cityTables: Int
)

data class PriceZoneRequest(
    @SerializedName("festival_id") val festivalId: Int,
    val name: String,
    @SerializedName("table_price") val tablePrice: Double,
    val tableTypes: List<TableTypeRequest>,
    val mapZoneIds: List<Int>? = null
)

data class TableTypeRequest(
    val name: String,
    @SerializedName("nb_total") val nbTotal: Int,
    @SerializedName("nb_total_player") val nbTotalPlayer: Int
)
