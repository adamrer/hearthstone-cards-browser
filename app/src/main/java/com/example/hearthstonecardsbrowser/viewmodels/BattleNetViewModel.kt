package com.example.hearthstonecardsbrowser.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.hearthstonecardsbrowser.Constants.ALL_CLASSES
import com.example.hearthstonecardsbrowser.Constants.ALL_RARITIES
import com.example.hearthstonecardsbrowser.Constants.ALL_TYPES
import com.example.hearthstonecardsbrowser.Constants.METADATA_CLASSES_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_RARITIES_NAME
import com.example.hearthstonecardsbrowser.Constants.METADATA_TYPES_NAME
import com.example.hearthstonecardsbrowser.api.CardRequest
import com.example.hearthstonecardsbrowser.api.MetadataItem
import com.example.hearthstonecardsbrowser.repository.CardsRepository
import com.example.hearthstonecardsbrowser.repository.Repository
import com.example.hearthstonecardsbrowser.ui.data.HearthstoneCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BattleNetViewModel : ViewModel() {
    private val _pageCount = mutableIntStateOf(0)
    val pageCount: MutableState<Int> = _pageCount

    var textFilter by mutableStateOf("")
    var classFilter by mutableStateOf(MetadataItem(-1, ALL_CLASSES, ""))
    var typeFilter by mutableStateOf(MetadataItem(-1, ALL_TYPES, ""))
    var rarityFilter by mutableStateOf(MetadataItem(-1, ALL_RARITIES, ""))
    var sortBy by mutableStateOf(MetadataItem(6, "Name", "name"))
    var descending by mutableStateOf(false)
    var isFiltersExpanded by mutableStateOf(false)
    var page by mutableStateOf(1)

    private val cardsRepository: Repository by lazy {
        CardsRepository()
    }

    private val _cards: MutableStateFlow<ViewModelResponseState<List<HearthstoneCard>, Int>> =
        MutableStateFlow(ViewModelResponseState.Idle)
    val cards: StateFlow<ViewModelResponseState<List<HearthstoneCard>, Int>>
        get() = _cards

    private val _metadata: MutableStateFlow<ViewModelResponseState<Map<String, Map<Int, MetadataItem>>, Int>> =
        MutableStateFlow(ViewModelResponseState.Idle)
    val metadata: StateFlow<ViewModelResponseState<Map<String, Map<Int, MetadataItem>>, Int>>
        get() = _metadata

    fun loadMetadata() {
        if (_metadata.value is ViewModelResponseState.Loading) {
            return
        }
        _metadata.value = ViewModelResponseState.Loading

        val metadataResult = mutableMapOf<String, Map<Int, MetadataItem>>()
        cardsRepository.getMetadata(METADATA_RARITIES_NAME) { rarities ->
            if (rarities != null) {
                val tempResult = HashMap(rarities)
                tempResult[-1] = MetadataItem(-1, ALL_RARITIES, "")
                metadataResult[METADATA_RARITIES_NAME] = tempResult

                cardsRepository.getMetadata(METADATA_CLASSES_NAME) { classes ->
                    if (classes != null) {
                        val tempResultClasses = HashMap(classes)
                        tempResultClasses[-1] = MetadataItem(-1, ALL_CLASSES, "")
                        metadataResult[METADATA_CLASSES_NAME] = tempResultClasses
                        cardsRepository.getMetadata(METADATA_TYPES_NAME) { types ->
                            if (types != null) {
                                val tempResultTypes = HashMap(types)
                                tempResultTypes[-1] = MetadataItem(-1, ALL_TYPES, "")
                                metadataResult[METADATA_TYPES_NAME] = tempResultTypes
                                _metadata.value = ViewModelResponseState.Success(metadataResult)
                            } else {
                                _metadata.value = ViewModelResponseState.Error(500)
                            }
                        }
                    } else {
                        _metadata.value = ViewModelResponseState.Error(500)
                    }
                }
            } else {
                _metadata.value = ViewModelResponseState.Error(500)
            }
        }
    }

    fun loadCards(cardRequest: CardRequest) {
        if (_cards.value is ViewModelResponseState.Loading) {
            return
        }

        _cards.value = ViewModelResponseState.Loading

        cardsRepository.getCards(cardRequest) { cardsResult, _, pageCount ->
            if (!cardsResult.isNullOrEmpty() && pageCount != null) {
                _cards.value = ViewModelResponseState.Success(cardsResult)
                _pageCount.intValue = pageCount
            } else {
                _cards.value = ViewModelResponseState.Error(500)
            }
        }
    }
}
