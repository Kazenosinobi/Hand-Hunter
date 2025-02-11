package ru.practicum.android.diploma.features.filters.domain.manager

import ru.practicum.android.diploma.features.filters.domain.model.Industry

interface SpecializationManager {
    fun getIndustry(): Industry
    fun keepIndustry(industry: Industry)
    fun acceptData()
    fun clearManager()
}

class SpecializationManagerImpl(
    private val filterManager: FilterManager
) : SpecializationManager {
    private var industry: Industry = getIndustryFromManager() ?: Industry(
        id = null,
        name = null
    )

    override fun getIndustry(): Industry {
        return industry
    }

    override fun keepIndustry(industry: Industry) {
        this.industry = industry
    }

    override fun acceptData() {
        filterManager.keepIndustry(industry)
    }

    override fun clearManager() {
        industry = getIndustryFromManager() ?: Industry(
            id = null,
            name = null
        )
    }

    private fun getIndustryFromManager(): Industry? {
        return filterManager.getIndustry()
    }
}
