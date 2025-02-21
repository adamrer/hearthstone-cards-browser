package com.example.hearthstonecardsbrowser.api

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import com.example.hearthstonecardsbrowser.HearthstoneCard
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class BattleNetViewModel : ViewModel() {
    private val _cards = mutableStateOf<List<HearthstoneCard>>(emptyList())
    val cards: State<List<HearthstoneCard>> = _cards

    private val _pageCount = mutableIntStateOf(0)
    val pageCount: MutableState<Int> = _pageCount

    private val _metadata = mutableStateOf<MutableMap<String, Map<Int, MetadataItem>>>(mutableMapOf())
    val metadata : State<Map<String, Map<Int, MetadataItem>>> = _metadata

    private val _cardRequest = mutableStateOf(CardRequest())
    var cardRequest : State<CardRequest> = _cardRequest

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage


    private val client = OkHttpClient()
    private val baseUrl = "https://us.api.blizzard.com/hearthstone/cards"
    private val locale = "en_US"
    private val clientId = "38254a25f2814cb4bb94ade89f3d6a6d"
    private val clientSecret = "eFrAlzvVXrELx9RY2073aam8Wz1lsrl9"

    private val authenticator = BattleNetAuthenticator(clientId, clientSecret)

    fun searchCards(cardRequest: CardRequest){
        _isLoading.value = true
        fetchCards(cardRequest){ cardsResult, _, pageCount ->
            if (!cardsResult.isNullOrEmpty() && pageCount != null) {
                _cards.value = cardsResult
                _pageCount.intValue = pageCount
                _isLoading.value = false
                _errorMessage.value = ""

            } else if (cardsResult == null) {
                _errorMessage.value = "Failed to load cards"
                _isLoading.value = false
            }
            else if (cardsResult.isEmpty()) {
                _errorMessage.value = "No cards found"
                _isLoading.value = false
            }
        }
    }

    fun searchMetadata(){

        fetchMetadata("rarities"){ result ->
            if (result != null){
                val tempResult = HashMap(result)
                tempResult[-1] = MetadataItem(-1, "Any", "")
                _metadata.value["rarities"] = tempResult
            }
        }
        fetchMetadata("classes"){ result ->
            if (result != null){
                val tempResult = HashMap(result)
                tempResult[-1] = MetadataItem(-1, "Any", "")
                _metadata.value["classes"] = tempResult
            }
        }
        fetchMetadata("types"){ result ->
            if (result != null){
                val tempResult = HashMap(result)
                tempResult[-1] = MetadataItem(-1, "Any", "")
                _metadata.value["types"] = tempResult
            }
        }
    }

    fun setCardRequest(cardRequest: CardRequest){
        this._cardRequest.value = cardRequest
    }

    private fun buildUrl(request: CardRequest): String {
        val builder = Uri.parse(baseUrl).buildUpon()
        builder.appendQueryParameter("locale", locale)
        if (!request.set.isNullOrEmpty()){
            builder.appendQueryParameter("set", request.set)
        }
        if (!request.classFilter.isNullOrEmpty()){
            builder.appendQueryParameter("class", request.classFilter)
        }
        if (!request.type.isNullOrEmpty()){
            builder.appendQueryParameter("type", request.type)
        }

        if (!request.rarity.isNullOrEmpty()){
            builder.appendQueryParameter("rarity", request.rarity)
        }
        if (!request.textFilter.isNullOrEmpty()){
            builder.appendQueryParameter("textFilter", request.textFilter)
        }
        if (!request.spellSchool.isNullOrEmpty()){
            builder.appendQueryParameter("spellSchool", request.spellSchool)
        }
        if (!request.sort.isNullOrEmpty()){
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

    private fun cardFromJson(cardJson: JSONObject) : HearthstoneCard {

        return HearthstoneCard(
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
            flavorText = cardJson.optString("flavorText")
        )
    }

    private fun fetchCards(filter: CardRequest, callback: (List<HearthstoneCard>?, Int?, Int?) -> Unit) { // callback(cards, page, pageCount)
        authenticator.getAccessToken { token ->
            if (token == null){
                callback(null, null, null)

            }
            else{

                val request = Request.Builder()
                    .url(buildUrl(filter))
                    .header("Authorization", "Bearer $token")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException){
                        e.printStackTrace()
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
                            for (i in 0 until cards.length()){
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

    private fun fetchMetadata(type:String, callback: (Map<Int, MetadataItem>?) -> Unit) { //
        authenticator.getAccessToken { token ->

            if (token == null){
                callback(null)

            }
            else{
                val url = "https://us.api.blizzard.com/hearthstone/metadata/$type?locale=$locale"

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException){
                        e.printStackTrace()
                        callback(null)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful){
                                callback(null)
                                return
                            }
                            val metadataMap = mutableMapOf<Int, MetadataItem>()

                            val metadataArray = JSONArray(response.body?.string() ?: "[]")

                            for (i in 0 until metadataArray.length()){
                                val metadataObject:JSONObject = metadataArray.getJSONObject(i)
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


}