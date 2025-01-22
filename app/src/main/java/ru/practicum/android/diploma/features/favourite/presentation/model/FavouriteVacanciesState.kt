package ru.practicum.android.diploma.features.favourite.presentation.model

sealed interface FavouriteVacanciesState {

    data object Empty : FavouriteVacanciesState

    data class Error(
        val errorMessage: String
    ) : FavouriteVacanciesState

    data class Content(
        val favouriteVacancies: List<VacancyUI>
    ) : FavouriteVacanciesState
}
