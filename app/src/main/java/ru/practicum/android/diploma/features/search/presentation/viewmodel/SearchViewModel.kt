package ru.practicum.android.diploma.features.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.common.presentation.ResourceProvider
import ru.practicum.android.diploma.features.search.domain.interactor.VacanciesSearchInteractor
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch
import ru.practicum.android.diploma.features.search.presentation.model.SearchState
import ru.practicum.android.diploma.features.search.presentation.model.toUI
import kotlin.coroutines.cancellation.CancellationException

class SearchViewModel(
    private val interactor: VacanciesSearchInteractor,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private val searchStateFlow = MutableStateFlow<SearchState>(SearchState.Init)
    fun getSearchStateFlow() = searchStateFlow.asStateFlow()

    private var lastSearchQuery: String? = null

    fun search(querySearch: QuerySearch) {
        val isStateError = when (searchStateFlow.value) {
            is SearchState.EmptyError, SearchState.ServerError, SearchState.NetworkError -> true
            else -> false
        }

        if (querySearch.text.isNullOrBlank() || querySearch.text == lastSearchQuery && !isStateError) return

        lastSearchQuery = querySearch.text.trim()
        viewModelScope.launch {
            searchStateFlow.emit(SearchState.Loading)
            interactor.getVacancies(querySearch)
                .onSuccess { searchStateFlow.emit(SearchState.Content(it.toUI(resourceProvider))) }
                .onFailure { handleError(it) }
        }

    }

    private suspend fun handleError(error: Throwable) {
        when (error) {
            is CustomException.NetworkError -> searchStateFlow.emit(SearchState.NetworkError)
            is CustomException.EmptyError -> searchStateFlow.emit(SearchState.EmptyError)
            is CustomException.ServerError -> searchStateFlow.emit(SearchState.ServerError)
            is CancellationException -> throw error
            else -> Unit
        }
    }

    fun onClearedSearch() {
        viewModelScope.launch {
            searchStateFlow.emit(SearchState.Init)
        }
        lastSearchQuery = null
    }

}
