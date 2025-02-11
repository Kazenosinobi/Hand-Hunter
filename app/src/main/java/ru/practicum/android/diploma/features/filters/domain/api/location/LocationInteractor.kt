package ru.practicum.android.diploma.features.filters.domain.api.location

import ru.practicum.android.diploma.features.filters.domain.model.Country
import ru.practicum.android.diploma.features.filters.domain.model.Region

interface LocationInteractor {
    suspend fun getCountriesList(params: Map<String, String>): Result<List<Country>>
    suspend fun getOriginalAreasList(params: Map<String, String>): Result<List<Region>>
    fun getSortedFilteredRegionsList(
        list: List<Region>,
        newList: MutableList<Region>,
        countryId: String?
    ): List<Region>
    fun setCountry(country: Country)
    fun setRegion(country: Country, region: Region)
    fun getCountryId(): String?
}
