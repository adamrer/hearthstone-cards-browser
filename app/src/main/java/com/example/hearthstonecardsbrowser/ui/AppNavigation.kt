import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hearthstonecardsbrowser.Constants.CARD_DETAIL_NAVIGATION
import com.example.hearthstonecardsbrowser.Constants.CARD_LIST_NAVIGATION
import com.example.hearthstonecardsbrowser.viewmodels.BattleNetViewModel
import com.example.hearthstonecardsbrowser.ui.CardDetailPage
import com.example.hearthstonecardsbrowser.ui.CardsPage
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = CARD_LIST_NAVIGATION) {
        composable(CARD_LIST_NAVIGATION) {
            val viewModel: BattleNetViewModel = viewModel()
            CardsPage(
                viewModel,
                navController,
                Modifier)
        }
        composable(CARD_DETAIL_NAVIGATION) {
            CardDetailPage(
                navController
            )
        }
    }
}
