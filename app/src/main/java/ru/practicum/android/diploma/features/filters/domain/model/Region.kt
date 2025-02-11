package ru.practicum.android.diploma.features.filters.domain.model

data class Region(
    val id: String?,
    val parentId: String?,
    val name: String?,
    val areas: List<Region>? = null
)
