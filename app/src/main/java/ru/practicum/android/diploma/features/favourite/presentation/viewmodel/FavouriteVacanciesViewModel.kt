package ru.practicum.android.diploma.features.favourite.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.common.presentation.ResourceProvider
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesInteractor
import ru.practicum.android.diploma.features.favourite.presentation.model.FavouriteVacanciesState
import ru.practicum.android.diploma.features.favourite.presentation.model.toUi

class FavouriteVacanciesViewModel(
    private val resourceProvider: ResourceProvider,
    private val interactor: FavouriteVacanciesInteractor,
) : ViewModel() {

    private var _state: MutableStateFlow<FavouriteVacanciesState> =
        MutableStateFlow(FavouriteVacanciesState.Loading)
    val state: StateFlow<FavouriteVacanciesState> = _state.asStateFlow()

    init {
        getFavourites()
    }

    private fun getFavourites() {
        viewModelScope.launch {
            try {
                interactor.getFavourites().collect { vacancies ->
                    _state.value = if (vacancies.isEmpty()) {
                        FavouriteVacanciesState.Empty
                    } else {
                        FavouriteVacanciesState.Content(
                            vacancies.map { it.toUi(resourceProvider) }
                        )
                    }
                }
            }catch (e: CustomException.DatabaseError) {
                _state.value = FavouriteVacanciesState.Error
            }
        }
    }
}
