package com.example.hearthstonecardsbrowser.api

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.hearthstonecardsbrowser.Constants.BASE_URL
import com.example.hearthstonecardsbrowser.Constants.LOCALE
import com.example.hearthstonecardsbrowser.repository.BattleNetAuthenticator
import com.example.hearthstonecardsbrowser.ui.data.CardDetail
import com.example.hearthstonecardsbrowser.repository.CardsRepository
import com.example.hearthstonecardsbrowser.repository.Repository
import com.example.hearthstonecardsbrowser.viewmodels.ViewModelResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CardDetailViewModel : ViewModel() {

    private val _cardDetail: MutableStateFlow<ViewModelResponseState<CardDetail, Int>> =
        MutableStateFlow(ViewModelResponseState.Idle)
    val card: StateFlow<ViewModelResponseState<CardDetail, Int>>
        get() = this._cardDetail

    private val _className = mutableStateOf<String?>(null)
    val className: State<String?> = _className

    private val _rarityName = mutableStateOf<String?>(null)
    val rarityName: State<String?> = _rarityName

    private val _typeName = mutableStateOf<String?>(null)
    val typeName: State<String?> = _typeName

    private val client = OkHttpClient()

    private val cardsRepository: Repository by lazy {
        CardsRepository()
    }

    private val authenticator = BattleNetAuthenticator

    fun findCardById(id: String) {
        _cardDetail.value = ViewModelResponseState.Loading
        cardsRepository.getCard(id){ cardResult ->
            if (cardResult != null) {
                this._cardDetail.value = ViewModelResponseState.Success(cardResult)
                if (cardResult.className != "null") {
                    cardResult.className?.let { findClassName(it) }
                } else {
                    cardResult.multiClassIds?.let { findClassesNames(it) }
                }
                cardResult.rarity?.let { findRaritiesName(it) }
                cardResult.type?.let { findTypesName(it) }
            } else  {
                this._cardDetail.value = ViewModelResponseState.Error(404)
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
