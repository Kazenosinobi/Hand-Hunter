package ru.practicum.android.diploma.features.filters.presentation.model

import ru.practicum.android.diploma.features.filters.domain.model.Country
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.domain.model.FullLocationData
import ru.practicum.android.diploma.features.filters.domain.model.Industry
import ru.practicum.android.diploma.features.filters.domain.model.Region
import ru.practicum.android.diploma.features.filters.presentation.model.ui.CountryUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.FilterUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.IndustryUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.RegionUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.WorkplaceLocationUI

fun CountryUI.toDomain(): Country {
    return Country(
        id = id,
        name = name
    )
}

fun RegionUI.toDomain(): Region {
    return Region(
        id = id,
        parentId = parentId,
        name = name
    )
}

fun IndustryUI.toDomain(): Industry {
    return Industry(
        id = id,
        name = name,
    )
}

fun Country.toUI(): CountryUI {
    return CountryUI(
        id = id,
        name = name
    )
}

fun Industry.toUI(): IndustryUI {
    return IndustryUI(
        id = id,
        name = name
    )
}

fun Region.toUI(): RegionUI {
    return RegionUI(
        id = id,
        parentId = parentId,
        name = name
    )
}

fun FilterMainData.toUI(): FilterUI {
    return FilterUI(
        country = country?.name,
        region = region?.name,
        industry = industry?.name,
        salary = salary,
        onlyWithSalary = isNeedToHideVacancyWithoutSalary,
    )
}

fun FullLocationData.toUI(): WorkplaceLocationUI {
    return WorkplaceLocationUI(
        country = country?.name,
        city = region?.name
    )
}

fun getPlaceOfWork(country: String?, region: String?): String? {
    return country?.let {
        var placeOfWork = country
        if (region != null) placeOfWork += ", $region"
        placeOfWork
    }
}
