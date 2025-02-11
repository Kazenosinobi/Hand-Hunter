package ru.practicum.android.diploma.features.filters.domain.api.workplace

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData

interface SelectWorkplaceInteractor {
    fun clearData()

    fun subscribeLocationData(): Flow<FullLocationData?>

    fun deleteCountryData()
    fun deleteRegionData()

    fun acceptLocationData()
}
