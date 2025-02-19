package com.example.hearthstonecardsbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.hearthstonecardsbrowser.api.BattleNetApiClient
import com.example.hearthstonecardsbrowser.api.BattleNetAuthenticator
import com.example.hearthstonecardsbrowser.ui.theme.HearthstoneCardsBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val clientId = "38254a25f2814cb4bb94ade89f3d6a6d"
        val clientSecret = "eFrAlzvVXrELx9RY2073aam8Wz1lsrl9"
        val auth = BattleNetAuthenticator(clientId, clientSecret)
        val client = BattleNetApiClient(auth)
        setContent {
            HearthstoneCardsBrowserTheme {
                CardsPage(client, Modifier)

            }
        }
    }
}
