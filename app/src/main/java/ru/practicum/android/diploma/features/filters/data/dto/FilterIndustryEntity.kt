package ru.practicum.android.diploma.features.filters.data.dto

data class FilterIndustryEntity(
    val id: String,
    val name: String
) {
    companion object {
        fun default() = FilterIndustryEntity(id = "", name = "")
    }
}
