import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hearthstonecardsbrowser.Constants.CARD_DETAIL_NAVIGATION
import com.example.hearthstonecardsbrowser.Constants.CARD_LIST_NAVIGATION
import com.example.hearthstonecardsbrowser.ui.CardDetailPage
import com.example.hearthstonecardsbrowser.ui.CardsPage
import com.example.hearthstonecardsbrowser.ui.viewmodels.BattleNetViewModel
import com.example.hearthstonecardsbrowser.ui.viewmodels.CardDetailViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = CARD_LIST_NAVIGATION) {
        composable(CARD_LIST_NAVIGATION) { backStackEntry ->
            val viewModel: BattleNetViewModel = viewModel(backStackEntry)
            CardsPage(viewModel, navController, Modifier)
        }
        composable(
            route = "$CARD_DETAIL_NAVIGATION/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val viewModel: CardDetailViewModel = viewModel(backStackEntry)
            val someParameter = backStackEntry.arguments?.getString("id") ?: ""
            CardDetailPage(someParameter, viewModel)
        }
    }
}
