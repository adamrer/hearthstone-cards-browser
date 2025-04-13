package com.example.hearthstonecardsbrowser.api

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.hearthstonecardsbrowser.Constants.BASE_URL
import com.example.hearthstonecardsbrowser.Constants.LOCALE
import com.example.hearthstonecardsbrowser.repository.BattleNetAuthenticator
import com.example.hearthstonecardsbrowser.ui.data.CardDetail
import com.example.hearthstonecardsbrowser.repository.CardsRepository
import com.example.hearthstonecardsbrowser.repository.Repository
import com.example.hearthstonecardsbrowser.viewmodels.ViewModelResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CardDetailViewModel : ViewModel() {

    private val _cardDetail: MutableStateFlow<ViewModelResponseState<CardDetail, Int>> =
        MutableStateFlow(ViewModelResponseState.Idle)
    val card: StateFlow<ViewModelResponseState<CardDetail, Int>>
        get() = this._cardDetail

    private val _className = mutableStateOf<String?>(null)
    val className: State<String?> = _className

    private val _rarityName = mutableStateOf<String?>(null)
    val rarityName: State<String?> = _rarityName

    private val _typeName = mutableStateOf<String?>(null)
    val typeName: State<String?> = _typeName

    private val client = OkHttpClient()

    private val cardsRepository: Repository by lazy {
        CardsRepository()
    }

    private val authenticator = BattleNetAuthenticator

    fun findCardById(id: String) {
        _cardDetail.value = ViewModelResponseState.Loading
        cardsRepository.getCard(id){ cardResult ->
            if (cardResult != null) {
                this._cardDetail.value = ViewModelResponseState.Success(cardResult)
                cardResult.className?.let { findClassName(it) }
                cardResult.rarity?.let { findRaritiesName(it) }
                cardResult.type?.let { findTypesName(it) }
            } else  {
                this._cardDetail.value = ViewModelResponseState.Error(404)
            }
        }
    }

    private fun findClassName(id: String) {
        cardsRepository.getMetadata("classes") { classesResult ->
            if (classesResult != null) {
                _className.value = classesResult[Integer.parseInt(id)]?.name
            }
        }
    }

    private fun findRaritiesName(id: String) {
        cardsRepository.getMetadata("rarities") { raritiesResult ->
            if (raritiesResult != null) {
                _rarityName.value = raritiesResult[Integer.parseInt(id)]?.name
            }
        }
    }

    private fun findTypesName(id: String) {
        cardsRepository.getMetadata("types") { typesResult ->
            if (typesResult != null) {
                _typeName.value = typesResult[Integer.parseInt(id)]?.name
            }
        }
    }



    private fun buildCardUrl(id: String): String {
        val builder = Uri.parse(BASE_URL).buildUpon()
        builder.appendPath(id)
        builder.appendQueryParameter("locale", LOCALE)
        return builder.build().toString()
    }

    private fun cardFromJson(cardJson: JSONObject): CardDetail =
        CardDetail(
            id = cardJson.optString("id"),
            name = cardJson.optString("name"),
            className = cardJson.optString("classId"),
            type = cardJson.optString("cardTypeId"),
            rarity = cardJson.optString("rarityId"),
            artist = cardJson.optString("artistName"),
            collectible = cardJson.optString("collectible"),
            flavorText = cardJson.optString("flavorText"),
            image = cardJson.optString("image"),
        )

    private fun getCard(
        id: String,
        callback: (CardDetail?) -> Unit,
    ) {
        authenticator.getAccessToken { token ->
            if (token == null) {
                callback(null)
            } else {
                val request =
                    Request
                        .Builder()
                        .url(buildCardUrl(id))
                        .header("Authorization", "Bearer $token")
                        .build()

                client.newCall(request).enqueue(
                    object : Callback {
                        override fun onFailure(
                            call: Call,
                            e: IOException,
                        ) {
                            e.printStackTrace()
                            callback(null)
                        }

                        override fun onResponse(
                            call: Call,
                            response: Response,
                        ) {
                            response.use {
                                if (!response.isSuccessful) {
                                    callback(null)
                                    return
                                }

                                val json = JSONObject(response.body?.string() ?: "{}")
                                val card = cardFromJson(json)
                                callback(card)
                            }
                        }
                    },
                )
            }
        }
    }
}
