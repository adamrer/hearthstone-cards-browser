package com.example.hearthstonecardsbrowser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hearthstonecardsbrowser.api.BattleNetViewModel
import com.example.hearthstonecardsbrowser.api.CardRequest
import com.example.hearthstonecardsbrowser.api.MetadataItem


@Composable
fun CardsPage(viewModel : BattleNetViewModel, navController: NavController, modifier: Modifier) {

    val sortByTypes:Map<Int, MetadataItem> = mapOf(
        0 to MetadataItem(0, "Mana Cost", "manaCost"),
        1 to MetadataItem(1, "Attack", "attack"),
        2 to MetadataItem(2, "Health", "health"),
        3 to MetadataItem(3, "Data Added", "dataAdded"),
        4 to MetadataItem(4, "Group by Class", "groupByClass"),
        5 to MetadataItem(5, "Class", "class"),
        6 to MetadataItem(6, "Name", "name")
        )


    val keyboardController = LocalSoftwareKeyboardController.current
    var textFilter :String by remember { mutableStateOf("") }
    var classFilter :MetadataItem by remember{ mutableStateOf(MetadataItem(-1, "Class", "")) }
    var typeFilter :MetadataItem by remember{ mutableStateOf(MetadataItem(-1, "Type", "")) }
    var rarityFilter :MetadataItem by remember{ mutableStateOf(MetadataItem(-1, "Rarity", ""))}
    var sortBy :MetadataItem by remember{ mutableStateOf(MetadataItem(6, "Name", "name")) }
    var descending :Boolean by remember{ mutableStateOf(false) }


    var page : Int by remember { mutableIntStateOf(1) }
    var isMetadataLoading by remember { mutableStateOf(true) }
    val pageCount by viewModel.pageCount
    val cards by viewModel.cards
    val metadata by viewModel.metadata
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.searchMetadata()
        isMetadataLoading = true
    }

    LaunchedEffect(metadata) {
        while (metadata.size < 3) {
            Thread.yield()
        }
        isMetadataLoading = false
    }


    val cardRequest = CardRequest(null, classFilter.slug, typeFilter.slug, rarityFilter.slug, textFilter, null, sortBy.slug, descending, page, 20)

    fun loadCards() {
        cardRequest.page = page
        viewModel.searchCards(cardRequest)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(93, 152, 245))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(Color.White)
        ){
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = textFilter,
                    onValueChange = { textFilter = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    MetadataDropDownMenu(metadata["classes"], classFilter) { classFilter = it }
                    MetadataDropDownMenu(metadata["types"], typeFilter) { typeFilter = it }
                    MetadataDropDownMenu(metadata["rarities"], rarityFilter) { rarityFilter = it }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Sort by:")
                    MetadataDropDownMenu(sortByTypes, sortBy) { sortBy = it }
                    Checkbox(checked = descending, onCheckedChange = { descending = it })
                    Text("Descending")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { keyboardController?.hide(); page = 1; loadCards() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Search")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading || isMetadataLoading -> CircularProgressIndicator()
            errorMessage.isNotEmpty() -> Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            else -> {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        if (page > 1) Button(onClick = { page--; loadCards() }) { Text("Prev") }
                        Text("Page $page of $pageCount", Modifier.padding(horizontal = 16.dp))
                        if (page < pageCount) Button(onClick = { page++; loadCards() }) { Text("Next") }
                    }
                    CardGridScreen(cards, navController)
                }
            }
        }
    }
}

@Composable
fun MetadataDropDownMenu(items: Map<Int, MetadataItem>?, selectedOption: MetadataItem, setOption: (MetadataItem) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(8.dp)) {
        Button(onClick = { expanded = true }) {
            Text(selectedOption.name.ifEmpty { "Select" })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items?.forEach { option ->
                DropdownMenuItem(text = { Text(option.value.name) }, onClick = { setOption(option.value); expanded = false })
            }
        }
    }
}

@Composable
fun CardGridScreen(cards: List<HearthstoneCard>, navController: NavController) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
        items(cards) { card ->
            CardItem(card) {
                navController.currentBackStackEntry?.savedStateHandle?.set("card", card)
                navController.navigate("cardDetail")
            }
        }
    }
}

@Composable
fun CardItem(card: HearthstoneCard, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick() }) {
        Column {
            Image(painter = rememberAsyncImagePainter(card.image), contentDescription = card.name, modifier = Modifier.fillMaxWidth().height(230.dp), contentScale = ContentScale.Crop)
            card.name?.let { Text(text = it, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium) }
        }
    }
}
