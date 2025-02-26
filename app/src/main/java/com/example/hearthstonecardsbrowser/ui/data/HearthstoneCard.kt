package com.example.hearthstonecardsbrowser.ui.data
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HearthstoneCard(
    val id: Int?,
    val collectible: Int?,
    val slug: String?,
    val classId: Int?,
    val cardTypeId: Int?,
    val cardSetId: Int?,
    val rarityId: Int?,
    val artistName: String?,
    val health: Int?,
    val attack: Int?,
    val manaCost: Int?,
    val name: String?,
    val flavorText: String?,
    val text: String?,
    val image: String?,
    val cropImage: String?,
) : Parcelable
