package ru.practicum.android.diploma.features.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.common.presentation.ResourceProvider
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.search.domain.interactor.VacanciesSearchInteractor
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch.Companion.DEFAULT_PAGE
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch.Companion.DEFAULT_PER_PAGE
import ru.practicum.android.diploma.features.search.domain.model.Vacancies
import ru.practicum.android.diploma.features.search.presentation.model.SearchState
import ru.practicum.android.diploma.features.search.presentation.model.VacanciesSearchUI
import ru.practicum.android.diploma.features.search.presentation.model.toUI
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.min

class SearchViewModel(
    private val interactor: VacanciesSearchInteractor,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    sealed class ToastEvent {
        data object NetworkError : ToastEvent()
    }

    private val searchStateFlow = MutableStateFlow<SearchState>(SearchState.Init)
    private val toastEventFlow = MutableSharedFlow<ToastEvent>(replay = 0)
    val networkErrorStateFlow = MutableStateFlow(false)
    private val isSearchWithFilters = MutableStateFlow(false)

    fun getSearchStateFlow() = searchStateFlow.asStateFlow()
    fun getToastEventFlow() = toastEventFlow.asSharedFlow()
    fun getNetworkErrorStateFlow() = networkErrorStateFlow.asStateFlow()
    fun isSearchWithFilters() = isSearchWithFilters.asStateFlow()

    private var currentPage = 0
    private var totalPages = 0
    private var totalFoundVacancies = 0
    private val loadedVacancies = mutableListOf<VacancySearchUI>()
    var isLoading = false
    private var lastSearchQuery: String? = null
    private var lastSearchQuerySearch: QuerySearch? = null
    private var filters: FilterMainData? = null

    private fun search(querySearch: QuerySearch, isPagination: Boolean = false) {
        val queryText = querySearch.text?.trim()
        val isStateError = when (searchStateFlow.value) {
            is SearchState.ServerError, SearchState.NetworkError -> true
            else -> false
        }
        val isQueryEmpty = queryText.isNullOrEmpty()
        val isSameQuerySearch = querySearch == lastSearchQuerySearch
        val shouldSkipSearch = isSameQuerySearch && !isPagination && !isStateError

        if (isQueryEmpty || shouldSkipSearch) return
        if (isLoading) return

        lastSearchQuery = queryText
        lastSearchQuerySearch = querySearch
        isLoading = true

        viewModelScope.launch {
            if (!isPagination) searchStateFlow.emit(SearchState.Loading)
            isLoading = true
            interactor.getVacancies(querySearch)
                .onSuccess {
                    handleSuccess(it, isPagination)
                }
                .onFailure {
                    handleError(it, isPagination)
                }
                .also {
                    isLoading = false
                }
        }
    }

    fun getFilters() {
        filters = interactor.getFilters()
        isSearchWithFilters.value =
            filters?.let { filters?.region != null ||
                filters?.country != null ||
                filters?.industry != null ||
                filters?.salary != null ||
                filters?.isNeedToHideVacancyWithoutSalary == true
            } == true
    }

    private suspend fun handleSuccess(vacancies: Vacancies, isPagination: Boolean) {
        networkErrorStateFlow.value = false
        currentPage = vacancies.page
        totalPages = min(vacancies.pages, MAX_ITEMS - 1)
        totalFoundVacancies = vacancies.found

        val newVacancies = vacancies.items.map { it.toUI(resourceProvider) }
        if (isPagination) {
            loadedVacancies.addAll(newVacancies)
        } else {
            loadedVacancies.clear()
            loadedVacancies.addAll(newVacancies)
        }

        val vacanciesUI = VacanciesSearchUI(
            items = loadedVacancies,
            found = vacancies.found,
            pages = totalPages,
            page = vacancies.page,
            perPage = vacancies.perPage
        )

        searchStateFlow.emit(SearchState.Content(vacanciesUI))
    }

    private suspend fun handleError(
        throwableError: Throwable,
        isLoadingNextPage: Boolean
    ) {
        isLoading = false
        if (isLoadingNextPage) {
            searchStateFlow.emit(
                SearchState.Content(
                    VacanciesSearchUI(
                        items = loadedVacancies,
                        found = totalFoundVacancies,
                        pages = totalPages,
                        page = currentPage,
                        perPage = DEFAULT_PER_PAGE
                    ),
                )
            )
            if (throwableError is CustomException.NetworkError) {
                networkErrorStateFlow.value = true
                toastEventFlow.emit(ToastEvent.NetworkError)
            }
        } else {
            when (throwableError) {
                is CustomException.NetworkError -> {
                    searchStateFlow.emit(SearchState.NetworkError)
                    networkErrorStateFlow.value = true
                }

                is CustomException.EmptyError -> searchStateFlow.emit(SearchState.EmptyError)
                is CustomException.ServerError -> searchStateFlow.emit(SearchState.ServerError)
                is CancellationException -> throw throwableError
                else -> Unit
            }
        }
    }

    @Suppress("LabeledExpression")
    fun loadNextPage() {
        if (!isLoading && currentPage < totalPages) {
            viewModelScope.launch {
                if (isLoading) return@launch

                searchStateFlow.emit(SearchState.Pagination)

                runCatching {
                    performSearch(
                        text = lastSearchQuery,
                        page = currentPage + 1,
                        isPagination = true
                    )
                }.onFailure { handleNextPageError(it) }
            }
        }
    }

    private suspend fun handleNextPageError(error: Throwable) {
        isLoading = false
        when (error) {
            is CustomException.NetworkError -> {
                if (searchStateFlow.value !is SearchState.NetworkError) {
                    searchStateFlow.emit(SearchState.NetworkError)
                }
            }
        }
    }

    fun onClearedSearch() {
        viewModelScope.launch {
            searchStateFlow.emit(SearchState.Init)
        }
        lastSearchQuery = null
        currentPage = DEFAULT_PAGE
        totalPages = Int.MAX_VALUE
        loadedVacancies.clear()
    }

    fun performSearch(
        text: String?,
        page: Int = DEFAULT_PAGE,
        perPage: Int = DEFAULT_PER_PAGE,
        isPagination: Boolean = false
    ) {
        search(
            QuerySearch(
                text = text,
                page = page,
                perPage = perPage,
                params = mapFiltersToMap()
            ),
            isPagination
        )
    }

    private fun mapFiltersToMap(): Map<String, String> {
        val area = when {
            !filters?.region?.id.isNullOrBlank() -> filters?.region?.id
            !filters?.country?.id.isNullOrBlank() -> filters?.country?.id
            else -> null
        }
        val industry = filters?.industry?.id
        val salary = filters?.salary
        val isNeedToHideVacancyWithoutSalary = filters?.isNeedToHideVacancyWithoutSalary

        val params = mutableMapOf<String, String>().apply {
            if (!area.isNullOrBlank()) put("area", area)
            if (!industry.isNullOrBlank()) put("industry", industry)
            if (!salary.isNullOrBlank()) put("salary", salary)
            if (isNeedToHideVacancyWithoutSalary == true) {
                put("only_with_salary", isNeedToHideVacancyWithoutSalary.toString())
            }
        }

        return params
    }

    override fun onCleared() {
        super.onCleared()
        filters = null
    }

    companion object {
        private const val MAX_ITEMS = 100
    }
}
