package com.example.hearthstonecardsbrowser.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hearthstonecardsbrowser.Constants.CARD_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_CLASSES_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_RARITIES_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_TYPES_NAME
import com.example.hearthstonecardsbrowser.api.MetadataItem
import com.example.hearthstonecardsbrowser.ui.data.HearthstoneCard

@Composable
fun CardDetailPage(navController: NavController) {
    val card = navController.previousBackStackEntry?.savedStateHandle?.get<HearthstoneCard>(CARD_NAME)
    val metadata =
        navController.previousBackStackEntry?.savedStateHandle?.get<Map<String, Map<Int, MetadataItem>>>(
            METADATA_NAME,
        )

    Scaffold { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = card?.name ?: "Unknown Card",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }

                    val image: Painter = rememberAsyncImagePainter(card?.image)

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = image,
                            contentDescription = card?.name,
                            modifier =
                                Modifier
                                    .width(200.dp)
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        card?.flavorText?.let {
                            Text(
                                text = "\"$it\"",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp),
                                textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DetailItem("Class", metadata?.get(METADATA_CLASSES_NAME)?.get(card?.classId)?.name)
                        DetailItem("Type", metadata?.get(METADATA_TYPES_NAME)?.get(card?.cardTypeId)?.name)
                        DetailItem("Rarity", metadata?.get(METADATA_RARITIES_NAME)?.get(card?.rarityId)?.name)
                        DetailItem("Artist", card?.artistName)
                        DetailItem("Collectible", if (card?.collectible == 1) "Yes" else "No")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value ?: "Unknown", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
