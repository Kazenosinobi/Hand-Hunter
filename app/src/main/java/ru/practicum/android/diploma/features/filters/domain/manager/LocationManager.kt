package ru.practicum.android.diploma.features.filters.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.practicum.android.diploma.features.filters.domain.model.Country
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData
import ru.practicum.android.diploma.features.filters.domain.model.Region

interface LocationManager {
    fun subscribeLocationData(): Flow<FullLocationData?>

    fun keepRegion(country: Country, region: Region)
    fun keepCountry(country: Country)

    fun getCountryId(): String?

    fun deleteCountry()
    fun deleteRegion()

    fun acceptData()
    fun clearManager()
}

class LocationManagerImpl(
    private val filterManager: FilterManager
) : LocationManager {
    private val flow: MutableStateFlow<FullLocationData> = MutableStateFlow(getLocationData())

    override fun subscribeLocationData(): Flow<FullLocationData?> {
        return flow.asStateFlow()
    }

    override fun keepRegion(country: Country, region: Region) {
        flow.value = flow.value.copy(
            country = country, region = region
        )
    }

    override fun keepCountry(country: Country) {
        var newValue = flow.value.copy(
            country = country
        )

        flow.value.region?.let {
            newValue = newValue.copy(region = null)
        }

        flow.value = newValue
    }

    override fun getCountryId(): String? {
        return flow.value.country?.id
    }

    override fun deleteCountry() {
        flow.value = flow.value.copy(country = null, region = null)
    }

    override fun deleteRegion() {
        flow.value = flow.value.copy(region = null)
    }

    override fun acceptData() {
        filterManager.keepWorkplace(flow.value)
    }

    override fun clearManager() {
        flow.value = filterManager.getLocationData()
    }

    private fun getLocationData(): FullLocationData {
        return filterManager.getLocationData()
    }
}
