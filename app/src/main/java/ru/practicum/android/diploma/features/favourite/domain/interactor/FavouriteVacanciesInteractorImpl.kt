package ru.practicum.android.diploma.features.favourite.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesInteractor
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesRepository

class FavouriteVacanciesInteractorImpl(
    private val repository: FavouriteVacanciesRepository
) : FavouriteVacanciesInteractor {

    override suspend fun addToFavourites(vacancy: VacancyDetails) {
        repository.addToFavourites(vacancy)
    }

    override fun getFavourites(): Flow<List<VacancyDetails>> {
        return repository.getFavourites()
    }

    override suspend fun deleteFavouriteVacancy(vacancyId: String) {
        repository.deleteFavouriteVacancy(vacancyId)
    }

}
