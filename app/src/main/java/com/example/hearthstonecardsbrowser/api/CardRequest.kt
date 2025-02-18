package com.example.hearthstonecardsbrowser.api

data class CardRequest (
    val set: String?,
    val classFilter: String?,
    val type: String?,
    val rarity: String?,
    val textFilter: String?,
    val spellSchool: String?,
    val sort: String?,
    val page: Int?,
    val pageSize: Int?
)