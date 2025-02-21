package com.example.hearthstonecardsbrowser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hearthstonecardsbrowser.api.MetadataItem

@Composable
fun CardDetailPage(navController: NavController) {
    val card = navController.previousBackStackEntry?.savedStateHandle?.get<HearthstoneCard>("card")
    val metadata = navController.previousBackStackEntry?.savedStateHandle?.get<Map<String, Map<Int, MetadataItem>>>("metadata")

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).background(Color.LightGray)) {
            Text(
                text = card?.name ?: "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            Row{
                Text(card?.flavorText ?: "")
            }
            Row {
                val image: Painter = rememberAsyncImagePainter(card?.image)

                Image(
                    painter = image,
                    contentDescription = card?.name,
                    modifier = Modifier
                        .fillMaxWidth(),

                )
            }
            Row{
                Text("Class: ${metadata?.get("classes")?.get(card?.classId)?.name}")

            }
            Row{
                Text("Type: ${metadata?.get("types")?.get(card?.cardTypeId)?.name}")

            }
            Row{
                Text("Rarity: ${metadata?.get("rarities")?.get(card?.rarityId)?.name}")
            }

            Row{
                Text("Artist: ${card?.artistName}")
            }
            Row{
                if (card?.collectible == 1){
                    Text("Collectible")
                }
                else{
                    Text("Not collectible")
                }
            }
        }
    }
}