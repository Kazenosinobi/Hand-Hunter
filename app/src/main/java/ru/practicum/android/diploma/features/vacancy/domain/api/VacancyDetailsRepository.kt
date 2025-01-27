package ru.practicum.android.diploma.features.vacancy.domain.api

import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails

interface VacancyDetailsRepository {
    suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails>

    suspend fun getFavouriteVacancy(vacancyId: String): VacancyDetails
}
