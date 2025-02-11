package ru.practicum.android.diploma.features.filters.domain.interactor

import ru.practicum.android.diploma.features.filters.domain.api.specialization.SpecializationInteractor
import ru.practicum.android.diploma.features.filters.domain.api.specialization.SpecializationRepository
import ru.practicum.android.diploma.features.filters.domain.manager.SpecializationManager
import ru.practicum.android.diploma.features.filters.domain.model.Industry

class SpecializationInteractorImpl(
    private val specializationRepository: SpecializationRepository,
    private val manager: SpecializationManager
) : SpecializationInteractor {

    override fun setIndustry(industry: Industry) {
        manager.keepIndustry(industry)
    }

    override fun acceptData() {
        manager.acceptData()
    }

    override suspend fun getIndustriesList(params: Map<String, String>): Result<List<Industry>> {
        return specializationRepository.getIndustriesList(params)
    }

    override suspend fun getSavedIndustry(): Industry? {
        val industry = manager.getIndustry()
        return if (industry.id != null && industry.name != null) industry else null
    }

    override fun clearManager() {
        manager.clearManager()
    }
}
