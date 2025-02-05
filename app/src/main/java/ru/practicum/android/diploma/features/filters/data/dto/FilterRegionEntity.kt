package ru.practicum.android.diploma.features.filters.data.dto

data class FilterRegionEntity(
    val id: String,
    val name: String,
    val parentId: String
) {
    companion object {
        fun default() = FilterRegionEntity(id = "", name = "", parentId = "")
    }
}
