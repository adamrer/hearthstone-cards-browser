package com.example.hearthstonecardsbrowser

import AppNavigation
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

        setContent {
            HearthstoneCardsBrowserTheme {
                AppNavigation()

            }
        }
    }
}
