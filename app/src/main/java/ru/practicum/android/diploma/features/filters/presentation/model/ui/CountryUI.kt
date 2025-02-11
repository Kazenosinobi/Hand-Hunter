package ru.practicum.android.diploma.features.filters.presentation.model.ui

import ru.practicum.android.diploma.features.filters.presentation.model.api.Regionable

data class CountryUI(
    override val id: String?,
    override val name: String?
) : Regionable()
