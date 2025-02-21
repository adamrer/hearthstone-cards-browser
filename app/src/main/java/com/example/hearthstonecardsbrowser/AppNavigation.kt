import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hearthstonecardsbrowser.CardDetailPage
import com.example.hearthstonecardsbrowser.CardsPage
import com.example.hearthstonecardsbrowser.api.BattleNetViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = BattleNetViewModel()


    NavHost(navController, startDestination = "cardList") {
        composable("cardList") { CardsPage(viewModel, navController, Modifier) }
        composable("cardDetail") {
            CardDetailPage(navController)
        }
    }
}