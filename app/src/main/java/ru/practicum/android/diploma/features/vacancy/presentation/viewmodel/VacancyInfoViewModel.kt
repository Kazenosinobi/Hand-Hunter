package ru.practicum.android.diploma.features.vacancy.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.common.presentation.ResourceProvider
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesInteractor
import ru.practicum.android.diploma.features.vacancy.domain.api.VacancyDetailsInteractor
import ru.practicum.android.diploma.features.vacancy.presentation.model.VacancyInfoUI
import ru.practicum.android.diploma.features.vacancy.presentation.model.toUI
import kotlin.coroutines.cancellation.CancellationException

class VacancyInfoViewModel(
    private val resourceProvider: ResourceProvider,
    private val vacancyDetailsInteractor: VacancyDetailsInteractor,
    private val favouriteVacanciesInteractor: FavouriteVacanciesInteractor,
    private val wasOpenedFromSearch: Boolean,
    private val vacancyId: String?
) : ViewModel() {
    private var isFavourite: Boolean = false
    private var details: VacancyDetails? = null

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    fun getVacancyInfo() {
        if (vacancyId == null) {
            _state.value = State.NoData
            return
        }

        if (wasOpenedFromSearch) {
            getVacancyInfoFromNetwork(vacancyId)
        } else {
            getVacancyInfoFromDb(vacancyId)
        }
    }

    private fun getVacancyInfoFromNetwork(vacancyId: String) {
        viewModelScope.launch {
            vacancyDetailsInteractor.getVacancyDetails(vacancyId)
                .onSuccess {
                    isFavourite = vacancyDetailsInteractor.isFavouriteVacancy(vacancyId)
                    details = it.copy(isFavourite = isFavourite)
                    _state.value =
                        State.Data(it.toUI(resourceProvider).copy(isFavourite = isFavourite))
                }
                .onFailure { handleError(it) }
        }
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is CustomException.RequestError, CustomException.EmptyError, CustomException.NetworkError -> {
                _state.value = State.NoData
            }

            is CustomException.ServerError -> _state.value = State.ServerError
            is CancellationException -> throw error
            else -> Unit
        }
    }

    private fun getVacancyInfoFromDb(vacancyId: String) {
        viewModelScope.launch {
            details = vacancyDetailsInteractor.getFavouriteVacancy(vacancyId)
            details?.let {
                _state.value = State.Data(it.toUI(resourceProvider))
            }
            isFavourite = true
        }
    }

    fun getVacancySharingText(): String {
        return details?.vacancyUrl ?: EMPTY_STRING
    }

    fun toggleFavouriteVacancy(): Boolean {
        if (_state.value is State.Data) {
            viewModelScope.launch {
                details?.let {
                    if (isFavourite) {
                        favouriteVacanciesInteractor.deleteFavouriteVacancy(it.id)
                    } else {
                        favouriteVacanciesInteractor.addToFavourites(it)
                    }
                }
            }
            isFavourite = !isFavourite
        }
        return isFavourite
    }

    override fun onCleared() {
        super.onCleared()
        details = null
    }

    sealed interface State {
        data class Data(val vacancyInfo: VacancyInfoUI) : State
        data object NoData : State
        data object ServerError : State
        data object Loading : State
    }

    companion object {
        private const val EMPTY_STRING = ""
    }
}
