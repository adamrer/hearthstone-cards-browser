package com.example.hearthstonecardsbrowser.repository

import android.net.Uri
import com.example.hearthstonecardsbrowser.Constants.BASE_URL
import com.example.hearthstonecardsbrowser.Constants.LOCALE
import com.example.hearthstonecardsbrowser.Constants.METADATA_URL
import com.example.hearthstonecardsbrowser.api.CardRequest
import com.example.hearthstonecardsbrowser.api.MetadataItem
import com.example.hearthstonecardsbrowser.ui.data.CardDetail
import com.example.hearthstonecardsbrowser.ui.data.HearthstoneCard
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CardsRepository: Repository {

    private val authenticator = BattleNetAuthenticator
    private val client = OkHttpClient()
    private val baseUrl = BASE_URL
    private val locale = LOCALE


    override fun getCards(
        cardRequest: CardRequest,
        callback: (List<HearthstoneCard>?, Int?, Int?) -> Unit
    ) {

        var errorMessage = "No cards found"
        authenticator.getAccessToken { token ->
            if (token == null) {
                errorMessage = "Invalid access token"
            } else {
                val request =
                    Request
                        .Builder()
                        .url(buildUrl(cardRequest))
                        .header("Authorization", "Bearer $token")
                        .build()

                client.newCall(request).enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        callback(null, null, null)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful){
                                callback(null, null, null)
                                return
                            }
                            val json = JSONObject(response.body?.string() ?: "{}")
                            val cards = json.getJSONArray("cards")
                            val page = json.getInt("page")
                            val pageCount = json.getInt("pageCount")

                            val cardList = mutableListOf<HearthstoneCard>()

                            for (i in 0 until cards.length()) {
                                val cardJson = cards.getJSONObject(i)
                                cardList.add(cardFromJson(cardJson))
                            }
                            callback(cardList, page, pageCount)
                        }
                    }
                })



            }
        }

    }

    private fun buildMetadataUrl(type: String): String {
        val builder = Uri.parse(METADATA_URL).buildUpon()
        builder.appendPath(type)
        builder.appendQueryParameter("locale", LOCALE)
        return builder.build().toString()
    }

    override fun getMetadata(
        type: String,
        callback: (Map<Int, MetadataItem>?) -> Unit
    ) {

        var errorMessage = "No metadata found"

        authenticator.getAccessToken { token ->

            if (token == null) {
                errorMessage = "Invalid access token"
            } else {

                val request =
                    Request
                        .Builder()
                        .url(buildMetadataUrl(type))
                        .header("Authorization", "Bearer $token")
                        .build()

                client.newCall(request).enqueue(object : Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        callback(null)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful){
                                callback(null)
                            }
                            val metadataMap = mutableMapOf<Int, MetadataItem>()
                            val metadataArray = JSONArray(response.body?.string() ?: "[]")

                            for (i in 0 until metadataArray.length()) {
                                val metadataObject: JSONObject = metadataArray.getJSONObject(i)
                                val id = metadataObject.getInt("id")
                                val name = metadataObject.getString("name")
                                val slug = metadataObject.getString("slug")
                                metadataMap[id] = MetadataItem(id, name, slug)
                            }
                            callback(metadataMap)
                        }
                    }
                })

            }
        }

    }

    private fun buildCardUrl(id: String): String {
        val builder = Uri.parse(BASE_URL).buildUpon()
        builder.appendPath(id)
        builder.appendQueryParameter("locale", LOCALE)
        return builder.build().toString()
    }

    override fun getCard(
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
                                val card = cardDetailFromJson(json)
                                callback(card)
                            }
                        }
                    },
                )
            }
        }
    }
    private fun buildUrl(request: CardRequest): String {
        val builder = Uri.parse(baseUrl).buildUpon()
        builder.appendQueryParameter("locale", locale)
        if (!request.set.isNullOrEmpty()) {
            builder.appendQueryParameter("set", request.set)
        }
        if (!request.classFilter.isNullOrEmpty()) {
            builder.appendQueryParameter("class", request.classFilter)
        }
        if (!request.type.isNullOrEmpty()) {
            builder.appendQueryParameter("type", request.type)
        }

        if (!request.rarity.isNullOrEmpty()) {
            builder.appendQueryParameter("rarity", request.rarity)
        }
        if (!request.textFilter.isNullOrEmpty()) {
            builder.appendQueryParameter("textFilter", request.textFilter)
        }
        if (!request.spellSchool.isNullOrEmpty()) {
            builder.appendQueryParameter("spellSchool", request.spellSchool)
        }
        if (!request.sort.isNullOrEmpty()) {
            var paramValue = request.sort
            if (request.descending != null) {
                paramValue += ":"
                paramValue +=
                    if (request.descending) {
                        "desc"
                    } else {
                        "asc"
                    }
            }
            builder.appendQueryParameter("sort", paramValue)
        }
        if (request.page != null) {
            builder.appendQueryParameter("page", request.page.toString())
        }
        if (request.pageSize != null) {
            builder.appendQueryParameter("pageSize", request.pageSize.toString())
        }
        return builder.build().toString()
    }

    private fun cardFromJson(cardJson: JSONObject): HearthstoneCard =
        HearthstoneCard(
            id = cardJson.optInt("id"),
            collectible = cardJson.optInt("collectible"),
            slug = cardJson.optString("slug"),
            classId = cardJson.optInt("classId"),
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
            cropImage = cardJson.optString("cropImage"),
            flavorText = cardJson.optString("flavorText"),
        )
    private fun cardDetailFromJson(cardJson: JSONObject): CardDetail =
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

}