import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hearthstonecardsbrowser.Constants.CARD_DETAIL_NAVIGATION
import com.example.hearthstonecardsbrowser.Constants.CARD_LIST_NAVIGATION
import com.example.hearthstonecardsbrowser.api.BattleNetViewModel
import com.example.hearthstonecardsbrowser.ui.CardDetailPage
import com.example.hearthstonecardsbrowser.ui.CardsPage

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel = BattleNetViewModel()

    NavHost(navController, startDestination = CARD_LIST_NAVIGATION) {
        composable(CARD_LIST_NAVIGATION) { CardsPage(viewModel, navController, Modifier) }
        composable(CARD_DETAIL_NAVIGATION) {
            CardDetailPage(navController)
        }
    }
}
