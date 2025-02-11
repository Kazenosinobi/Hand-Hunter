package ru.practicum.android.diploma.features.filters.data.service

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.features.filters.data.dto.FilterCountryEntity
import ru.practicum.android.diploma.features.filters.data.dto.FilterIndustryEntity
import ru.practicum.android.diploma.features.filters.data.dto.FilterMainDataEntity
import ru.practicum.android.diploma.features.filters.data.dto.FilterRegionEntity
import ru.practicum.android.diploma.features.filters.data.dto.FullLocationDataEntity

interface FilterStorage {
    fun saveData(data: FilterMainDataEntity)
    fun deleteFilterMainData()

    fun getFilterMainData(): FilterMainDataEntity
    fun getFullLocationData(): FullLocationDataEntity?
    fun getIndustry(): FilterIndustryEntity?
    fun getCountryId(): String?
}

class FilterStorageImpl(
    private val sharedPrefs: SharedPreferences
) : FilterStorage {

    override fun saveData(data: FilterMainDataEntity) {
        sharedPrefs.edit {
            putString(COUNTRY, Gson().toJson(data.country))
            putString(REGION, Gson().toJson(data.region))
            putString(INDUSTRY, Gson().toJson(data.industry))
            putString(SALARY, data.salary)
            putBoolean(SHOW_WITHOUT_SALARY_FLAG, data.isNeedToHideVacancyWithoutSalary)
        }
    }

    override fun getFilterMainData(): FilterMainDataEntity {
        val country = getCountryFromPrefs()
        val region = getRegionFromPrefs()
        val industry = getIndustry()
        val salary = sharedPrefs.getString(SALARY, null)
        val isNeedToHideVacancyWithoutSalary = sharedPrefs.getBoolean(SHOW_WITHOUT_SALARY_FLAG, false)
        return FilterMainDataEntity(country, region, industry, salary, isNeedToHideVacancyWithoutSalary)
    }

    override fun getFullLocationData(): FullLocationDataEntity? {
        val country = getCountryFromPrefs()
        val region = getRegionFromPrefs()
        return if (isCountryAndRegionNull(country, region)) {
            null
        } else {
            FullLocationDataEntity(country, region)
        }
    }

    override fun getCountryId(): String? {
        return getCountryFromPrefs()?.id
    }

    override fun getIndustry(): FilterIndustryEntity? {
        return Gson().fromJson(
            sharedPrefs.getString(INDUSTRY, null),
            object : TypeToken<FilterIndustryEntity?>() {}.type
        )
    }

    override fun deleteFilterMainData() {
        sharedPrefs.edit {
            remove(COUNTRY)
            remove(REGION)
            remove(INDUSTRY)
            remove(SALARY)
            remove(SHOW_WITHOUT_SALARY_FLAG)
        }
    }

    private fun getCountryFromPrefs(): FilterCountryEntity? {
        return Gson().fromJson(
            sharedPrefs.getString(COUNTRY, null),
            object : TypeToken<FilterCountryEntity?>() {}.type
        )
    }

    private fun getRegionFromPrefs(): FilterRegionEntity? {
        return Gson().fromJson(
            sharedPrefs.getString(REGION, null),
            object : TypeToken<FilterRegionEntity?>() {}.type
        )
    }

    private fun isCountryAndRegionNull(country: FilterCountryEntity?, region: FilterRegionEntity?): Boolean {
        return country == null && region == null
    }

    companion object {
        private const val COUNTRY = "country"
        private const val REGION = "region"
        private const val INDUSTRY = "industry"
        private const val SALARY = "salary"
        private const val SHOW_WITHOUT_SALARY_FLAG = "showWithoutSalary"
    }
}
