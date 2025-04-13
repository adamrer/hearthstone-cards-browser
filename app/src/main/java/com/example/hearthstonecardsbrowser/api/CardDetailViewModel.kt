package com.example.hearthstonecardsbrowser.api

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.hearthstonecardsbrowser.repository.BattleNetAuthenticator
import com.example.hearthstonecardsbrowser.repository.CardsRepository
import com.example.hearthstonecardsbrowser.repository.Repository
import com.example.hearthstonecardsbrowser.ui.data.CardDetail
import okhttp3.OkHttpClient

class CardDetailViewModel : ViewModel() {
    private val _cardDetail = mutableStateOf<CardDetail?>(null)
    val cardDetail: State<CardDetail?> = _cardDetail

    private val _className = mutableStateOf<String?>(null)
    val className: State<String?> = _className

    private val _rarityName = mutableStateOf<String?>(null)
    val rarityName: State<String?> = _rarityName

    private val _typeName = mutableStateOf<String?>(null)
    val typeName: State<String?> = _typeName

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val client = OkHttpClient()

    private val cardsRepository: Repository by lazy {
        CardsRepository()
    }

    private val authenticator = BattleNetAuthenticator

    fun findCardById(id: String) {
        _isLoading.value = true
        cardsRepository.getCard(id) { cardResult ->
            if (cardResult != null) {
                _cardDetail.value = cardResult
                if (cardResult.className != "null") {
                    cardResult.className?.let { findClassName(it) }
                } else {
                    cardResult.multiClassIds?.let { findClassesNames(it) }
                }
                cardResult.rarity?.let { findRaritiesName(it) }
                cardResult.type?.let { findTypesName(it) }
                _isLoading.value = false
            } else {
                _isLoading.value = false
            }
        }
    }

    private fun findClassName(id: String) {
        cardsRepository.getMetadata("classes") { classesResult ->
            if (classesResult != null) {
                _className.value = classesResult[Integer.parseInt(id)]?.name
            }
        }
    }

    private fun findClassesNames(ids: List<String>) {
        cardsRepository.getMetadata("classes") { classesResult ->
            if (classesResult != null) {
                _className.value =
                    classesResult.filter { e -> ids.contains(e.key.toString()) }.map { e -> e.value.name }.joinToString("\n")
            }
        }
    }

    private fun findRaritiesName(id: String) {
        cardsRepository.getMetadata("rarities") { raritiesResult ->
            if (raritiesResult != null) {
                _rarityName.value = raritiesResult[Integer.parseInt(id)]?.name
            }
        }
    }

    private fun findTypesName(id: String) {
        cardsRepository.getMetadata("types") { typesResult ->
            if (typesResult != null) {
                _typeName.value = typesResult[Integer.parseInt(id)]?.name
            }
        }
    }
}
