package ru.practicum.android.diploma.features.filters.data.dto

data class FilterMainDataEntity(
    val country: FilterCountryEntity?,
    val region: FilterRegionEntity?,
    val industry: FilterIndustryEntity?,
    val salary: String?,
    val isNeedToHideVacancyWithoutSalary: Boolean,
)
