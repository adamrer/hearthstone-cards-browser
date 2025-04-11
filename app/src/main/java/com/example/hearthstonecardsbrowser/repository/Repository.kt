package com.example.hearthstonecardsbrowser.repository

import com.example.hearthstonecardsbrowser.api.CardRequest
import com.example.hearthstonecardsbrowser.api.MetadataItem
import com.example.hearthstonecardsbrowser.ui.data.HearthstoneCard
import okhttp3.Callback

interface Repository {
    fun getCards(
        cardRequest: CardRequest,
        callback: (List<HearthstoneCard>?, Int?, Int?) -> Unit
    )
    fun getMetadata(
        type: String,
        callback: (Map<Int, MetadataItem>?) -> Unit
    )
}