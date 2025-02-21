package com.example.hearthstonecardsbrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
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
    var classFilter :MetadataItem by remember{ mutableStateOf(MetadataItem(-1, "Any", "")) }
    var typeFilter :MetadataItem by remember{ mutableStateOf(MetadataItem(-1, "Any", "")) }
    var rarityFilter :MetadataItem by remember{ mutableStateOf(MetadataItem(-1, "Any", ""))}
    var sortBy :MetadataItem by remember{ mutableStateOf(MetadataItem(6, "Name", "name")) }
    var descending :Boolean by remember{ mutableStateOf(false) }


    var page : Int by remember { mutableIntStateOf(1) }
    val pageCount by viewModel.pageCount
    val pageSize = 20

    val cardRequest : CardRequest = CardRequest(
        null,
        classFilter.slug,
        typeFilter.slug,
        rarityFilter.slug,
        textFilter,
        null,
        sortBy.slug,
        descending,
        page,
        pageSize)

    val cards by viewModel.cards
    val metadata by viewModel.metadata

    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading

    viewModel.searchMetadata()



    fun loadCards(cardRequest: CardRequest) {

        viewModel.searchCards(cardRequest)
    }




    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(93, 152, 245))
            .paddingFromBaseline(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Spacer(modifier = Modifier.height(50.dp))
        Row {
            OutlinedTextField(
                value = textFilter,
                onValueChange = {
                    textFilter = it
                },
                label = {
                    Text(text = "Text")
                }
            )
        }
        Row (verticalAlignment = Alignment.CenterVertically){
            Text("Class: ")
            MetadataDropDownMenu(metadata["classes"], classFilter, {newValue -> classFilter = newValue})
        }
        Row (verticalAlignment = Alignment.CenterVertically){
            Text("Type: ")
            MetadataDropDownMenu(metadata["types"], typeFilter, {newValue -> typeFilter = newValue})
        }
        Row (verticalAlignment = Alignment.CenterVertically){
            Text("Rarity: ")
            MetadataDropDownMenu(metadata["rarities"], rarityFilter, {newValue -> rarityFilter = newValue})
        }
        Row (verticalAlignment = Alignment.CenterVertically){
            Text("Sort by: ")
            MetadataDropDownMenu(sortByTypes, sortBy, {newValue -> sortBy = newValue})
            Text("DESC")
            Checkbox(
                checked = descending,
                onCheckedChange = {descending = it}
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Button(onClick = {
                keyboardController?.hide()
                cardRequest.page = 1
                loadCards(cardRequest)
            }){
                Text("Search")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage.isNotEmpty() -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                Column {

                    Row(
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        if (page > 1) {
                            Button(onClick = {
                                page -= 1
                                cardRequest.page = page
                                loadCards(cardRequest)
                            }) {
                                Text("Prev")
                            }

                        }
                        Text("Page $page of $pageCount")
                        if (page < pageCount) {
                            Button(onClick = {
                                page += 1
                                cardRequest.page = page
                                loadCards(cardRequest)
                            }) {
                                Text("Next")
                            }
                        }
                    }
                    Row {
                        CardGridScreen(cards, navController, metadata)
                    }
                }
            }
        }



    }
}

@Composable
fun MetadataDropDownMenu(items: Map<Int, MetadataItem>?, selectedOption: MetadataItem, setOption: (MetadataItem) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row (verticalAlignment = Alignment.CenterVertically){
        Box {
            Button(onClick = { expanded = true }) {

                if (selectedOption.name.isEmpty()){
                    Text("Choose")
                }
                else{
                    Text(selectedOption.name)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items?.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.value.name) },
                        onClick = {
                            setOption(option.value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

}


@Composable
fun CardGridScreen(cards: List<HearthstoneCard>, navController: NavController, metadata: Map<String, Map<Int, MetadataItem>>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.background(Color.White).fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(cards) { card ->
            CardItem(card){
                navController.currentBackStackEntry?.savedStateHandle?.set("card", card)
                navController.currentBackStackEntry?.savedStateHandle?.set("metadata", metadata)
                navController.navigate("cardDetail")
            }
        }
    }
}

@Composable
fun CardItem(card: HearthstoneCard, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable{onClick()},
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            val image: Painter = rememberAsyncImagePainter(card.image)

            Image(
                painter = image,
                contentDescription = card.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = card.name.toString(),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

