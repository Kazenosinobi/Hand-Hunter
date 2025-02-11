package ru.practicum.android.diploma.features.filters.domain.interactor

import ru.practicum.android.diploma.features.filters.domain.api.location.LocationInteractor
import ru.practicum.android.diploma.features.filters.domain.api.location.LocationRepository
import ru.practicum.android.diploma.features.filters.domain.manager.LocationManager
import ru.practicum.android.diploma.features.filters.domain.model.Country
import ru.practicum.android.diploma.features.filters.domain.model.Region

class LocationInteractorImpl(
    private val locationRepository: LocationRepository,
    private val locationManager: LocationManager
) : LocationInteractor {
    override suspend fun getCountriesList(params: Map<String, String>): Result<List<Country>> {
        return locationRepository.getCountriesList(params)
    }

    override suspend fun getOriginalAreasList(params: Map<String, String>): Result<List<Region>> {
        return locationRepository.getAllAreasList(params)
    }

    override fun getSortedFilteredRegionsList(
        list: List<Region>,
        newList: MutableList<Region>,
        countryId: String?
    ): List<Region> {
        var listWithoutOtherRegions = list.filter { it.id != OTHER_COUNTRIES_ID }

        countryId?.let { listWithoutOtherRegions = listWithoutOtherRegions.filter { it.id == countryId } }

        return filterRegionsList(listWithoutOtherRegions, newList).sortedBy { it.name }
    }

    override fun getCountryId(): String? {
        return locationManager.getCountryId()
    }

    private fun filterRegionsList(
        list: List<Region>,
        newList: MutableList<Region>
    ): List<Region> {
        list.forEach {
            if (it.areas.isNullOrEmpty()) {
                newList.add(it)
            } else {
                filterRegionsList(it.areas, newList)
            }
        }
        return newList
    }

    override fun setCountry(country: Country) {
        locationManager.keepCountry(country)
    }

    override fun setRegion(country: Country, region: Region) {
        locationManager.keepRegion(country, region)
    }

    companion object {
        private const val OTHER_COUNTRIES_ID = "1001"
    }
}
