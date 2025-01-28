package ru.practicum.android.diploma.features.vacancy.domain.interactor

import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.vacancy.domain.api.VacancyDetailsInteractor
import ru.practicum.android.diploma.features.vacancy.domain.api.VacancyDetailsRepository

class VacancyDetailsInteractorImpl(
    private val vacancyDetailsRepository: VacancyDetailsRepository
) : VacancyDetailsInteractor {
    override suspend fun getVacancyDetails(vacancyId: String): Result<VacancyDetails> {
        return vacancyDetailsRepository.getVacancyDetails(vacancyId)
    }

    override suspend fun getFavouriteVacancy(vacancyId: String): VacancyDetails {
        return vacancyDetailsRepository.getFavouriteVacancy(vacancyId)
    }

    override suspend fun isFavouriteVacancy(vacancyId: String): Boolean {
        return vacancyDetailsRepository.isFavouriteVacancy(vacancyId)
    }

}
