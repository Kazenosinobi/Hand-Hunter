package ru.practicum.android.diploma.features.filters.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.practicum.android.diploma.features.filters.domain.api.filter.FilterRepository
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData
import ru.practicum.android.diploma.features.filters.domain.model.Industry

@Suppress("TooManyFunctions")
interface FilterManager {
    fun subscribeData(): Flow<FilterMainData>
    fun getIndustry(): Industry?
    fun getLocationData(): FullLocationData
    fun getDataFromPrefs(): FilterMainData

    fun keepWorkplace(workplace: FullLocationData)
    fun keepIndustry(industry: Industry)
    fun keepSalary(salary: String)
    fun keepOnlyWithSalaryFlag(flag: Boolean)

    fun deleteWorkplace()
    fun deleteIndustry()
    fun deleteSalary()

    fun saveData()
    fun resetData()

    fun clearManager()
}

class FilterManagerImpl(
    private val filterRepository: FilterRepository
) : FilterManager {
    private val flow: MutableStateFlow<FilterMainData> = MutableStateFlow(getData())

    override fun subscribeData(): Flow<FilterMainData> {
        return flow.asStateFlow()
    }

    override fun getIndustry(): Industry? {
        return flow.value.industry
    }

    override fun getLocationData(): FullLocationData {
        return FullLocationData(flow.value.country, flow.value.region)
    }

    override fun getDataFromPrefs(): FilterMainData {
        return filterRepository.getFilterMainData()
    }

    override fun keepWorkplace(workplace: FullLocationData) {
        flow.value = flow.value.copy(
            country = workplace.country,
            region = workplace.region
        )
    }

    override fun keepIndustry(industry: Industry) {
        flow.value = flow.value.copy(
            industry = industry
        )
    }

    override fun keepSalary(salary: String) {
        flow.value = flow.value.copy(salary = salary)
    }

    override fun keepOnlyWithSalaryFlag(flag: Boolean) {
        flow.value = flow.value.copy(isNeedToHideVacancyWithoutSalary = flag)
    }

    override fun deleteWorkplace() {
        flow.value = flow.value.copy(country = null, region = null)
    }

    override fun deleteIndustry() {
        flow.value = flow.value.copy(industry = null)
    }

    override fun deleteSalary() {
        flow.value = flow.value.copy(salary = null)
    }

    override fun saveData() {
        filterRepository.saveData(flow.value)
    }

    override fun resetData() {
        filterRepository.deleteData()
        flow.value = FilterMainData(
            country = null,
            region = null,
            industry = null,
            salary = null,
            isNeedToHideVacancyWithoutSalary = false
        )
    }

    override fun clearManager() {
        flow.value = getData()
    }

    private fun getData(): FilterMainData {
        return filterRepository.getFilterMainData()
    }
}
