package ru.practicum.android.diploma.features.selectlocation.domain.interactor

import ru.practicum.android.diploma.features.selectlocation.domain.api.LocationInteractor
import ru.practicum.android.diploma.features.selectlocation.domain.api.LocationRepository
import ru.practicum.android.diploma.features.selectlocation.domain.model.Country
import ru.practicum.android.diploma.features.selectlocation.domain.model.Region

class LocationInteractorImpl(
    private val locationRepository: LocationRepository
) : LocationInteractor {
    override suspend fun getCountriesList(params: Map<String, String>): Result<List<Country>> {
        return locationRepository.getCountriesList(params)
    }

    override suspend fun getAllAreasList(params: Map<String, String>): Result<List<Region>> {
        return locationRepository.getAllAreasList(params).map { list ->
            list.filter { it.parentId.isNotEmpty() }
        }
    }

    override suspend fun getAllAreasByIdList(countryId: String, params: Map<String, String>): Result<List<Region>> {
        return locationRepository.getAllAreasByIdList(countryId, params)
    }

    override fun setCountry(country: Country) {
        locationRepository.setCountry(country)
    }

    override fun setRegion(region: Region) {
        locationRepository.setRegion(region)
    }
}
