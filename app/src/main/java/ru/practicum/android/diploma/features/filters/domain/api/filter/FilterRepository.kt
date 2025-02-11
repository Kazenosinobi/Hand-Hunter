package ru.practicum.android.diploma.features.filters.domain.api.filter

import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData
import ru.practicum.android.diploma.features.filters.domain.model.Industry

interface FilterRepository {
    fun saveData(data: FilterMainData)
    fun getFilterMainData(): FilterMainData
    fun getFullLocationData(): FullLocationData?
    fun getSavedIndustry(): Industry?
    fun deleteData()
}
