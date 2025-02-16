package com.example.greetingcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardsPage(modifier: Modifier = Modifier) {
    var text_filter by remember {
        mutableStateOf("")
    }
    var class_filter by remember{
        mutableStateOf("")
    }
    var type_filter by remember{
        mutableStateOf("")
    }
    var sort_by by remember{
        mutableStateOf("")
    }
    var descending by remember{
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth().background(Color(93, 152, 245)).paddingFromBaseline(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,


        ) {
        Row {
            OutlinedTextField(
                value = text_filter,
                onValueChange = {
                    text_filter = it
                },
                label = {
                    Text(text = "Search by text")
                }
            )
        }
        Row {
            OutlinedTextField(
                value = class_filter,
                onValueChange = {
                    class_filter = it
                },
                label = {
                    Text(text = "Search by class")
                }
            )
        }
        Row {
            OutlinedTextField(
                value = type_filter,
                onValueChange = {
                    type_filter = it
                },
                label = {
                    Text(text = "Search by type")
                }
            )
        }
        Row {
            OutlinedTextField(
                value = sort_by,
                onValueChange = {
                    sort_by = it
                },
                label = {
                    Text(text = "Sort by")
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Descending")
            Checkbox(
                checked = descending,
                onCheckedChange = {descending = it}
            )
            Button(onClick = { /*TODO*/}){
                Text("Search")
            }
        }
    }
}
