package ru.practicum.android.diploma.features.filters.data.dto

import ru.practicum.android.diploma.features.common.data.network.dto.area.AreaEntity
import ru.practicum.android.diploma.features.common.data.network.dto.area.CountryEntity
import ru.practicum.android.diploma.features.common.data.network.dto.industry.IndustryEntity
import ru.practicum.android.diploma.features.filters.domain.model.Country
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData
import ru.practicum.android.diploma.features.filters.domain.model.Industry
import ru.practicum.android.diploma.features.filters.domain.model.Region

// методы из SharedPref в domain
fun FilterMainDataEntity.toDomain(): FilterMainData {
    return FilterMainData(
        country = country?.toDomain(),
        region = region?.toDomain(),
        industry = industry?.toDomain(),
        salary = salary,
        isNeedToHideVacancyWithoutSalary = isNeedToHideVacancyWithoutSalary
    )
}

fun FullLocationDataEntity.toDomain(): FullLocationData {
    return FullLocationData(
        country = country?.toDomain(),
        region = region?.toDomain()
    )
}

private fun FilterCountryEntity.toDomain(): Country {
    return Country(
        id = id,
        name = name
    )
}

private fun FilterRegionEntity.toDomain(): Region {
    return Region(
        id = id,
        name = name,
        parentId = parentId,
    )
}

fun FilterIndustryEntity.toDomain(): Industry? {
    return if (id.isNullOrEmpty() && name.isNullOrEmpty()) {
        null
    } else {
        Industry(id = id, name = name)
    }
}

// методы из сети в domain
fun CountryEntity.toDomain(): Country {
    return Country(
        id = id,
        name = name
    )
}

fun AreaEntity.toDomain(): Region {
    return Region(
        id = id,
        parentId = parentId ?: "",
        name = name,
        areas = areas?.map { it.toDomain() } ?: listOf()
    )
}

fun IndustryEntity.toDomain(): Industry {
    return Industry(
        id = id,
        name = name
    )
}

// методы из domain в SharedPref
fun FilterMainData.toEntity(): FilterMainDataEntity {
    return FilterMainDataEntity(
        country = country?.toEntity(),
        region = region?.toEntity(),
        industry = industry?.toEntity(),
        salary = salary,
        isNeedToHideVacancyWithoutSalary = isNeedToHideVacancyWithoutSalary
    )
}

private fun Country.toEntity(): FilterCountryEntity {
    return FilterCountryEntity(
        id = id,
        name = name
    )
}

private fun Region.toEntity(): FilterRegionEntity {
    return FilterRegionEntity(
        id = id,
        name = name,
        parentId = parentId
    )
}

private fun Industry.toEntity(): FilterIndustryEntity {
    return FilterIndustryEntity(
        id = id,
        name = name
    )
}

