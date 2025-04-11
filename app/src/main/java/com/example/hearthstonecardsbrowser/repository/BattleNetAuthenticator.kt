package com.example.hearthstonecardsbrowser.repository

import com.example.hearthstonecardsbrowser.Constants.CLIENT_ID
import com.example.hearthstonecardsbrowser.Constants.CLIENT_SECRET
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object BattleNetAuthenticator {
    private var accessToken: String? = null
    private val client = OkHttpClient()
    private var clientId: String = CLIENT_ID
    private var clientSecret: String = CLIENT_SECRET

    fun setClientId(newClientId: String){
        clientId = newClientId
    }

    fun setClientSecret(newClientSecret: String){
        clientSecret = newClientSecret
    }

    fun getAccessToken(callback: (String?) -> Unit) {

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