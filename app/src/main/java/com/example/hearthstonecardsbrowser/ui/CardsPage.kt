package com.example.hearthstonecardsbrowser.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hearthstonecardsbrowser.Constants.ATTACK_ATTR
import com.example.hearthstonecardsbrowser.Constants.CARD_DETAIL_NAVIGATION
import com.example.hearthstonecardsbrowser.Constants.CARD_NAME
import com.example.hearthstonecardsbrowser.Constants.CLASS_ATTR
import com.example.hearthstonecardsbrowser.Constants.CLASS_FILTER_NAME
import com.example.hearthstonecardsbrowser.Constants.DATA_ADDED_ATTR
import com.example.hearthstonecardsbrowser.Constants.DESCENDING_NAME
import com.example.hearthstonecardsbrowser.Constants.GROUP_BY_CLASS_ATTR
import com.example.hearthstonecardsbrowser.Constants.HEALTH_ATTR
import com.example.hearthstonecardsbrowser.Constants.MANA_COST_ATTR
import com.example.hearthstonecardsbrowser.Constants.METADATA_CLASSES_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_RARITIES_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_TYPES_NAME
import com.example.hearthstonecardsbrowser.Constants.NAME_ATTR
import com.example.hearthstonecardsbrowser.Constants.PAGE_NAME
import com.example.hearthstonecardsbrowser.Constants.RARITY_FILTER_NAME
import com.example.hearthstonecardsbrowser.Constants.SORT_BY_NAME
import com.example.hearthstonecardsbrowser.Constants.TEXT_FILTER_NAME
import com.example.hearthstonecardsbrowser.Constants.TYPE_FILTER_NAME
import com.example.hearthstonecardsbrowser.viewmodels.BattleNetViewModel
import com.example.hearthstonecardsbrowser.api.BattleNetViewModel
import com.example.hearthstonecardsbrowser.api.CardRequest
import com.example.hearthstonecardsbrowser.api.MetadataItem
import com.example.hearthstonecardsbrowser.ui.data.HearthstoneCard
import com.example.hearthstonecardsbrowser.viewmodels.ViewModelResponseState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardsPage(
    viewModel: BattleNetViewModel,
    navController: NavController,
    modifier: Modifier,
) {
    LaunchedEffect(Unit) {
        viewModel.loadMetadata()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val pageCount by viewModel.pageCount

    val cardsState by viewModel.cards.collectAsStateWithLifecycle()
    val metadataState by viewModel.metadata.collectAsStateWithLifecycle()

    val cardRequest =
        CardRequest(
            null,
            viewModel.classFilter.slug,
            viewModel.typeFilter.slug,
            viewModel.rarityFilter.slug,
            viewModel.textFilter,
            null,
            viewModel.sortBy.slug,
            viewModel.descending,
            viewModel.page,
            20,
        )

    fun loadCards() {
        cardRequest.page = viewModel.page
        viewModel.loadCards(cardRequest)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(93, 152, 245))
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = viewModel.textFilter,
                        onValueChange = { viewModel.textFilter = it },
                        label = { Text("Search") },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.isFiltersExpanded = !viewModel.isFiltersExpanded }) {
                        Text(if (viewModel.isFiltersExpanded) "Hide Filters" else "Show Filters")
                    }
                }

                AnimatedVisibility(visible = viewModel.isFiltersExpanded) {
                    Column {

                        when (val metadState = metadataState){
                            is ViewModelResponseState.Idle -> Unit
                            is ViewModelResponseState.Success ->
                            {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    MetadataDropDownMenu(metadState.content[METADATA_CLASSES_NAME], viewModel.classFilter) { viewModel.classFilter = it }
                                    MetadataDropDownMenu(metadState.content[METADATA_TYPES_NAME], viewModel.typeFilter) { viewModel.typeFilter = it }
                                    MetadataDropDownMenu(metadState.content[METADATA_RARITIES_NAME], viewModel.rarityFilter) { viewModel.rarityFilter = it }
                                }

                                FlowRow(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier =
                                        Modifier
                                            .padding(top = 8.dp)
                                            .fillMaxWidth(),
                                ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .padding(vertical = 10.dp)
                                        .height(60.dp),
                            ) {
                                Text("Sort by:", modifier = Modifier.align(Alignment.CenterVertically))

                                MetadataDropDownMenu(
                                    mapOf(
                                        0 to MetadataItem(0, "Mana Cost", MANA_COST_ATTR),
                                        1 to MetadataItem(1, "Attack", ATTACK_ATTR),
                                        2 to MetadataItem(2, "Health", HEALTH_ATTR),
                                        3 to MetadataItem(3, "Date Added", DATA_ADDED_ATTR),
                                        4 to MetadataItem(4, "Group by Class", GROUP_BY_CLASS_ATTR),
                                        5 to MetadataItem(5, "Class", CLASS_ATTR),
                                        6 to MetadataItem(6, "Name", NAME_ATTR),
                                    ),
                                    viewModel.sortBy,
                                ) { viewModel.sortBy = it }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier
                                        .padding(vertical = 10.dp)
                                        .height(60.dp),
                            ) {
                                Switch(
                                    checked = viewModel.descending,
                                    onCheckedChange = { viewModel.descending = it },
                                    modifier =
                                        Modifier
                                            .padding(horizontal = 10.dp),
                                )

                                Text(
                                    text = if (viewModel.descending) "Descending" else "Ascending",
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }
                        }
                            }
                            is ViewModelResponseState.Error ->
                                Text(
                                    text = "Sorry, we have encountered an error: " + metadState.error,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(horizontal = 24.dp)
                                )
                            ViewModelResponseState.Loading ->
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.page = 1
                        loadCards()
                        viewModel.isFiltersExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Search")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        when (val cardState = cardsState) {
            is ViewModelResponseState.Idle -> Unit
            is ViewModelResponseState.Success ->
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                    ) {
                        if (pageCount != 0) {
                            if (viewModel.page > 1) {
                                Button(onClick = {
                                    viewModel.page--
                                    loadCards()
                                }) { Text("Prev") }
                            }
                            Text("Page ${viewModel.page} of $pageCount", Modifier.padding(horizontal = 16.dp))
                            if (viewModel.page < pageCount) {
                                Button(onClick = {
                                    viewModel.page++
                                    loadCards()
                                }) { Text("Next") }
                            }
                        }
                    }
                    when (val metaState = metadataState){
                        is ViewModelResponseState.Idle -> Unit
                        is ViewModelResponseState.Success ->
                            CardGridScreen(cardState.content, navController, metaState.content)
                        is ViewModelResponseState.Error ->
                            Text(
                                text = "Sorry, we have encountered an error: " + metaState.error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(horizontal = 24.dp)
                            )
                        is ViewModelResponseState.Loading ->
                            CircularProgressIndicator()
                    }
                }
            is ViewModelResponseState.Error ->
                Text(
                    text = "Sorry, we have encountered an error: " + cardState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 24.dp)
                )
            is ViewModelResponseState.Loading ->
                CircularProgressIndicator()
        }
    }
}

@Composable
fun MetadataDropDownMenu(
    items: Map<Int, MetadataItem>?,
    selectedOption: MetadataItem,
    setOption: (MetadataItem) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(8.dp)) {
        Button(onClick = { expanded = true }) {
            Text(selectedOption.name.ifEmpty { "Select" })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items?.forEach { option ->
                DropdownMenuItem(text = { Text(option.value.name) }, onClick = {
                    setOption(option.value)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun CardGridScreen(
    cards: List<HearthstoneCard>,
    navController: NavController,
    metadata: Map<String, Map<Int, MetadataItem>>,
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
        items(cards) { card ->
            CardItem(card) {
                navController.navigate("$CARD_DETAIL_NAVIGATION/${card.id}")
            }
        }
    }
}

@Composable
fun CardItem(
    card: HearthstoneCard,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onClick() },
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(card.image),
                contentDescription = card.name,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                contentScale = ContentScale.Crop,
            )
            card.name?.let { Text(text = it, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium) }
        }
    }
}
