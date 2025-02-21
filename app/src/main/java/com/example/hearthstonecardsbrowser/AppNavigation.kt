import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hearthstonecardsbrowser.CardDetailPage
import com.example.hearthstonecardsbrowser.CardsPage
import com.example.hearthstonecardsbrowser.HearthstoneCard
import com.example.hearthstonecardsbrowser.api.BattleNetApiClient
import com.example.hearthstonecardsbrowser.api.BattleNetAuthenticator

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val clientId = "38254a25f2814cb4bb94ade89f3d6a6d"
    val clientSecret = "eFrAlzvVXrELx9RY2073aam8Wz1lsrl9"
    val auth = BattleNetAuthenticator(clientId, clientSecret)
    val client = BattleNetApiClient(auth)



    NavHost(navController, startDestination = "cardList") {
        composable("cardList") { CardsPage(client, navController, Modifier) }
        composable("cardDetail") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("id")
            var card:HearthstoneCard? by remember { mutableStateOf(null) }
            client.getCard(cardId!!){ loadCard ->
                if (loadCard != null){
                    card = loadCard
                }
            }
            CardDetailPage(navController)
        }
    }
}