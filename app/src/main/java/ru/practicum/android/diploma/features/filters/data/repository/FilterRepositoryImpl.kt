package ru.practicum.android.diploma.features.filters.data.repository

import ru.practicum.android.diploma.features.filters.data.dto.toDomain
import ru.practicum.android.diploma.features.filters.data.dto.toEntity
import ru.practicum.android.diploma.features.filters.data.service.FilterStorage
import ru.practicum.android.diploma.features.filters.domain.api.filter.FilterRepository
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData
import ru.practicum.android.diploma.features.filters.domain.model.Industry

class FilterRepositoryImpl(
    private val filterStorage: FilterStorage
) : FilterRepository {
    override fun getFilterMainData(): FilterMainData {
        return filterStorage.getFilterMainData().toDomain()
    }

    override fun getFullLocationData(): FullLocationData? {
        return filterStorage.getFullLocationData()?.toDomain()
    }

    override fun getSavedIndustry(): Industry? {
        return filterStorage.getIndustry()?.toDomain()
    }

    override fun saveData(data: FilterMainData) {
        filterStorage.saveData(data.toEntity())
    }

    override fun deleteData() {
        filterStorage.deleteFilterMainData()
    }
}

