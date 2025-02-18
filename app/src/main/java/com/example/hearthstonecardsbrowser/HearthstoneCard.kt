package com.example.hearthstonecardsbrowser
import org.json.JSONArray

data class HearthstoneCard (
    val id: Int,
    val collectible: Boolean,
    val slug: String,
    val classId: Int,
    val multiClassIds: JSONArray,
    val cardTypeId: Int,
    val cardSetId: Int,
    val rarityId: Int?,

    val artistName: String?,

    val health: Int,
    val attack: Int,
    val manaCost: Int,
    val name: String,
    val text: String,
    val image: String,
    val cropImage: String
)