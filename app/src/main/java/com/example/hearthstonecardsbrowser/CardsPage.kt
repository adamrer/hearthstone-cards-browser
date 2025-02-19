package com.example.hearthstonecardsbrowser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.hearthstonecardsbrowser.api.BattleNetApiClient
import com.example.hearthstonecardsbrowser.api.BattleNetAuthenticator
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.hearthstonecardsbrowser.api.CardRequest



@Composable
fun CardsPage(client: BattleNetApiClient, modifier: Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFilter by remember { mutableStateOf("") }
    var classFilter by remember{ mutableStateOf("") }
    var typeFilter by remember{ mutableStateOf("") }
    var sortBy by remember{ mutableStateOf("") }
    var descending by remember{ mutableStateOf(false) }
    var page : Int = 1
    val pageSize = 10

    val cardRequest : CardRequest = CardRequest(
        null,
        classFilter,
        typeFilter,
        null,
        textFilter,
        null,
        sortBy,
        descending,
        page,
        pageSize)

    var cards by remember { mutableStateOf<List<HearthstoneCard>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadCards(cardRequest: CardRequest) {
        isLoading = true
        errorMessage = null

        client.getCards(
            cardRequest,
            { newCards, _ ->
                isLoading = false
                if (newCards == null) {
                    errorMessage = "Failed to load cards."
                } else if (newCards.isEmpty()){
                    errorMessage = "No cards found."
                } else {
                    cards = newCards
                }
            }
        )
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
        Row {
            OutlinedTextField(
                value = classFilter,
                onValueChange = {
                    classFilter = it
                },
                label = {
                    Text(text = "Class")
                }
            )
        }
        Row {
            Column{

            }
            OutlinedTextField(
                value = typeFilter,
                onValueChange = {
                    typeFilter = it
                },
                label = {
                    Text(text = "Type")
                }
            )
        }
        Row {
            Column{
                OutlinedTextField(
                    value = sortBy,
                    onValueChange = {
                        sortBy = it
                    },
                    label = {
                        Text(text = "Sort by")
                    }
                )
            }
            Column{
                Text("DESC")
                Checkbox(
                    checked = descending,
                    onCheckedChange = {descending = it}
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Button(onClick = {
                keyboardController?.hide()
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
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            cards != null -> {
                CardGridScreen(cards!!)
            }

        }

    }
}


@Composable
fun CardGridScreen(cards: List<HearthstoneCard>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.background(Color.White).fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(cards) { card ->
            CardItem(card)
        }
    }
}

@Composable
fun CardItem(card: HearthstoneCard) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            val image: Painter = rememberAsyncImagePainter(card.image)

            Image(
                painter = image,
                contentDescription = card.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
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

