package ru.practicum.android.diploma.features.favourite.presentation.viewmodel

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        interactor.getFavourites()
            .catch { _state.value = FavouriteVacanciesState.DatabaseError }
            .onEach{ vacancies ->
                _state.value = if (vacancies.isEmpty()) {
                    FavouriteVacanciesState.Empty
                } else {
                    FavouriteVacanciesState.Content(
                        vacancies.map { it.toUi(resourceProvider) }
                    )
                }
            }.launchIn(viewModelScope)
    }
}
