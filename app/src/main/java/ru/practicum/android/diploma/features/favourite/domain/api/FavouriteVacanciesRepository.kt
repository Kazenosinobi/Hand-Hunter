package ru.practicum.android.diploma.features.favourite.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails

interface FavouriteVacanciesRepository {

    suspend fun addToFavourites(vacancy: VacancyDetails)

    fun getFavourites(): Flow<List<VacancyDetails>>

    suspend fun deleteFavouriteVacancy(vacancyId: String)

}
