package com.example.hearthstonecardsbrowser.api

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.hearthstonecardsbrowser.Constants.BASE_URL
import com.example.hearthstonecardsbrowser.Constants.LOCALE
import com.example.hearthstonecardsbrowser.Constants.METADATA_URL
import com.example.hearthstonecardsbrowser.ui.data.CardDetail
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CardDetailViewModel : ViewModel() {
    private val _cardDetail = mutableStateOf<CardDetail?>(null)
    val cardDetail: State<CardDetail?> = _cardDetail

    private val _className = mutableStateOf<String?>(null)
    val className: State<String?> = _className

    private val _rarityName = mutableStateOf<String?>(null)
    val rarityName: State<String?> = _rarityName

    private val _typeName = mutableStateOf<String?>(null)
    val typeName: State<String?> = _typeName

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val client = OkHttpClient()

    private val authenticator = BattleNetAuthenticator

    fun findCardById(id: String) {
        _isLoading.value = true
        fetchCard(id) { cardResult ->
            if (cardResult != null) {
                _cardDetail.value = cardResult
                cardResult.className?.let { findClassName(it) }
                cardResult.rarity?.let { findRaritiesName(it) }
                cardResult.type?.let { findTypesName(it) }
                _isLoading.value = false
            } else {
                _isLoading.value = false
            }
        }
    }

    private fun findClassName(id: String) {
        fetchMetadata(id, "classes") { classResult ->
            if (classResult != null) {
                _className.value = classResult
            }
        }
    }

    private fun findRaritiesName(id: String) {
        fetchMetadata(id, "rarities") { rarityResult ->
            if (rarityResult != null) {
                _rarityName.value = rarityResult
            }
        }
    }

    private fun findTypesName(id: String) {
        fetchMetadata(id, "types") { typeResult ->
            if (typeResult != null) {
                _typeName.value = typeResult
            }
        }
    }

    private fun fetchMetadata(
        id: String,
        type: String,
        callback: (String?) -> Unit,
    ) {
        authenticator.getAccessToken { token ->
            if (token == null) {
                callback(null)
            } else {
                val request =
                    Request
                        .Builder()
                        .url(buildMetadataUrl(type))
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

                                val json = JSONArray(response.body?.string() ?: "[]")
                                for (i in 0 until json.length()) {
                                    val classJson = json.getJSONObject(i)
                                    if (classJson.optString("id") == id) {
                                        callback(classJson.optString("name"))
                                        return
                                    }
                                }
                            }
                        }
                    },
                )
            }
        }
    }

    private fun buildMetadataUrl(type: String): String {
        val builder = Uri.parse(METADATA_URL).buildUpon()
        builder.appendPath(type)
        builder.appendQueryParameter("locale", LOCALE)
        return builder.build().toString()
    }

    private fun buildUrl(id: String): String {
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

    private fun fetchCard(
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
                        .url(buildUrl(id))
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
