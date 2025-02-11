package ru.practicum.android.diploma.features.filters.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.features.filters.domain.api.workplace.SelectWorkplaceInteractor
import ru.practicum.android.diploma.features.filters.domain.manager.LocationManager
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData

class SelectWorkplaceInteractorImpl(
    private val locationManager: LocationManager
) : SelectWorkplaceInteractor {
    override fun clearData() {
        locationManager.clearManager()
    }

    override fun subscribeLocationData(): Flow<FullLocationData?> {
        return locationManager.subscribeLocationData()
    }

    override fun deleteCountryData() {
        locationManager.deleteCountry()
    }

    override fun deleteRegionData() {
        locationManager.deleteRegion()
    }

    override fun acceptLocationData() {
        locationManager.acceptData()
    }
}
