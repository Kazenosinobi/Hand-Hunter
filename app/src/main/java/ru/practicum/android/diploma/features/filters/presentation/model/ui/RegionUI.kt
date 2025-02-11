package ru.practicum.android.diploma.features.filters.presentation.model.ui

import ru.practicum.android.diploma.features.filters.presentation.model.api.Regionable

data class RegionUI(
    override val id: String?,
    val parentId: String?,
    override val name: String?
) : Regionable()
