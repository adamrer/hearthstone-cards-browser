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
fun CardGridScreen(cards: List<HearthstoneCard>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 sloupce
        modifier = Modifier.fillMaxSize(),
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
                contentDescription = "Obrázek karty ${card.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = card.name,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ShowCards(client: BattleNetApiClient, cardRequest: CardRequest) {
    var cards = listOf<HearthstoneCard>()

    client.getCards(cardRequest,
        { newCards:List<HearthstoneCard>?, currPage:Int? ->
            if (newCards == null){
                // no cards

            }
            else{
                cards = newCards
            }
        })
    CardGridScreen(cards)
}

@Composable
fun CardsPage(modifier: Modifier = Modifier) {
    var textFilter by remember {
        mutableStateOf("")
    }
    var classFilter by remember{
        mutableStateOf("")
    }
    var typeFilter by remember{
        mutableStateOf("")
    }
    var sortBy by remember{
        mutableStateOf("")
    }
    var descending by remember{
        mutableStateOf(false)
    }
    var page : Int = 0
    val pageSize = 10

    val clientId = "38254a25f2814cb4bb94ade89f3d6a6d"
    val clientSecret = "eFrAlzvVXrELx9RY2073aam8Wz1lsrl9"
    val auth = BattleNetAuthenticator(clientId, clientSecret)
    val client = BattleNetApiClient(auth)
    val cardRequest : CardRequest = CardRequest(null, classFilter, typeFilter, null, textFilter, null, sortBy, page, pageSize)



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(93, 152, 245))
            .paddingFromBaseline(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,


        ) {
        Row {
            OutlinedTextField(
                value = textFilter,
                onValueChange = {
                    textFilter = it
                },
                label = {
                    Text(text = "Search by text")
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
                    Text(text = "Search by class")
                }
            )
        }
        Row {
            OutlinedTextField(
                value = typeFilter,
                onValueChange = {
                    typeFilter = it
                },
                label = {
                    Text(text = "Search by type")
                }
            )
        }
        Row {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Descending")
            Checkbox(
                checked = descending,
                onCheckedChange = {descending = it}
            )
            Button(onClick = { /*TODO*/ }){
                Text("Search")
            }
        }

    }
}
