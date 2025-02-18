package com.example.hearthstonecardsbrowser.api

import okhttp3.Callback
import okhttp3.Call
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class BattleNetAuthenticator (
    private val clientId: String,
    private val clientSecret: String
) {
    private var accessToken: String? = null
    private val client = OkHttpClient()

    fun getAccessToken(callback: (String?) -> Unit) {
        if (accessToken != null){
            callback(accessToken)
            return
        }

        val url = "https://oauth.battle.net/token"
        val credentials = Credentials.basic(clientId, clientSecret)

        val requestBody = "grant_type=client_credentials".toRequestBody()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", credentials)
            .header("Content-type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException){
                e.printStackTrace()
                callback(null)
            }
            override fun onResponse(call: Call, response: Response){
                response.use{
                    if (!response.isSuccessful){
                        callback(null)
                        return
                    }
                    val json = JSONObject(response.body?.string() ?: "{}")
                    accessToken = json.optString("access_token", null)
                    callback(accessToken)
                }
            }
        })
    }
}