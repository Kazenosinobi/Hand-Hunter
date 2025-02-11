package ru.practicum.android.diploma.features.filters.presentation.model.state

import ru.practicum.android.diploma.features.filters.presentation.model.ui.FilterUI

sealed interface SearchFilterState {
    data class Content(
        val data: FilterUI,
        val isButtonsVisible: Boolean = false,
    )
}
