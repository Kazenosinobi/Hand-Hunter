package ru.practicum.android.diploma.features.vacancy.data.repository

import ru.practicum.android.diploma.features.common.data.database.FavouritesDao
import ru.practicum.android.diploma.features.common.data.network.service.NetworkClient
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.favourite.data.dto.toDomain
import ru.practicum.android.diploma.features.vacancy.data.dto.toDomain
import ru.practicum.android.diploma.features.vacancy.domain.api.VacancyDetailsRepository

class VacancyDetailsRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favouritesDao: FavouritesDao
) : VacancyDetailsRepository {

    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return networkClient.getVacancyById(vacancyId, mapOf()).map { it.toDomain() }
    }

    override suspend fun getFavouriteVacancy(vacancyId: String): VacancyDetails {
        val pair = favouritesDao.getFavouriteVacancy(vacancyId)
        return pair.first.toDomain(pair.second)
    }

    override suspend fun isFavouriteVacancy(vacancyId: String): Boolean {
        return favouritesDao.isExisted(vacancyId)
    }

}
