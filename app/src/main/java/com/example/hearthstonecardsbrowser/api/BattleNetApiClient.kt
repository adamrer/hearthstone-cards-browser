package com.example.hearthstonecardsbrowser.api

import android.net.Uri
import com.example.hearthstonecardsbrowser.HearthstoneCard
import okhttp3.Callback
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class BattleNetApiClient (
    private val authenticator: BattleNetAuthenticator
){
    private val client = OkHttpClient()
    private val baseUrl = "https://us.api.blizzard.com/hearthstone/cards"

    private fun buildUrl(baseUrl: String, request: CardRequest): String {
        val builder = Uri.parse(baseUrl).buildUpon()
        builder.appendQueryParameter("locale", "en_US")
        if (request.set != null){
            builder.appendQueryParameter("set", request.set)
        }
        if (request.classFilter != null){
            builder.appendQueryParameter("classFilter", request.classFilter)
        }
        if (request.type != null){
            builder.appendQueryParameter("type", request.type)
        }
        if (request.rarity != null){
            builder.appendQueryParameter("rarity", request.rarity)
        }
        if (request.textFilter != null){
            builder.appendQueryParameter("textFilter", request.textFilter)
        }
        if (request.spellSchool != null){
            builder.appendQueryParameter("spellSchool", request.spellSchool)
        }
        if (request.sort != null){
            var paramValue = request.sort
            if (request.descending != null){
                paramValue += ":"
                paramValue += if (request.descending){
                    "desc"
                } else{
                    "asc"
                }
            }
            builder.appendQueryParameter("sort", paramValue)
        }
        if (request.page != null){
            builder.appendQueryParameter("page", request.page.toString())
        }
        if (request.pageSize != null){
            builder.appendQueryParameter("pageSize", request.pageSize.toString())
        }
        return builder.build().toString()
    }

    fun getCards(filter: CardRequest, callback: (List<HearthstoneCard>?, Int?) -> Unit) { // callback(cards, page)
        authenticator.getAccessToken { token ->
            if (token == null){
                callback(null, null)

            }
            else{

                val request = Request.Builder()
                    .url(buildUrl(baseUrl, filter))
                    .header("Authorization", "Bearer $token")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException){
                        e.printStackTrace()
                        callback(null, null)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful){
                                callback(null, null)
                                return
                            }
                            val json = JSONObject(response.body?.string() ?: "{}")
                            val cards = json.getJSONArray("cards")
                            val page = json.getInt("page")
                            val pageCount = json.getInt("pageCount")

                            val cardList = mutableListOf<HearthstoneCard>()
                            for (i in 0 until cards.length()){
                                val cardJson = cards.getJSONObject(i)
                                cardList.add(
                                    HearthstoneCard(
                                        id = cardJson.optInt("id"),
                                        collectible = cardJson.optInt("collectible"),
                                        slug = cardJson.optString("slug"),
                                        classId = cardJson.optInt("classId"),
                                        multiClassIds = cardJson.getJSONArray("multiClassIds"),
                                        cardTypeId = cardJson.optInt("cardTypeId"),
                                        cardSetId = cardJson.optInt("cardSetId"),
                                        rarityId = cardJson.optInt("rarityId"),
                                        artistName = cardJson.optString("artistName"),
                                        health = cardJson.optInt("health"),
                                        attack = cardJson.optInt("attack"),
                                        manaCost = cardJson.optInt("manaCost"),
                                        name = cardJson.optString("name"),
                                        text = cardJson.optString("text"),
                                        image = cardJson.optString("image"),
                                        cropImage = cardJson.optString("cropImage")
                                    )
                                )
                            }
                            callback(cardList, page)

                        }
                    }
                })
            }
        }
    }

    fun getMetadata(callback: (Map<String, Map<Int, String>>) -> Unit){ //

    }
}