package ru.practicum.android.diploma.features.common.presentation.models

import ru.practicum.android.diploma.features.filters.domain.model.Country
import ru.practicum.android.diploma.features.filters.domain.model.Industry
import ru.practicum.android.diploma.features.filters.domain.model.FilterMainData
import ru.practicum.android.diploma.features.filters.domain.model.FilterRegion
import ru.practicum.android.diploma.features.filters.presentation.model.ui.FilterUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.CountryUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.RegionUI
import ru.practicum.android.diploma.features.filters.presentation.model.ui.IndustryUI

fun CountryUI.toDomain(): Country {
    return Country(
        id = id,
        name = name,
    )
}

fun Country.toUI(): CountryUI {
    return CountryUI(
        id = id,
        name = name,
    )
}

fun IndustryUI.toDomain(): Industry {
    return Industry(
        id = id,
        name = name,
    )
}

fun Industry.toUI(): IndustryUI {
    return IndustryUI(
        id = id,
        name = name,
    )
}

fun RegionUI.toDomain(): FilterRegion {
    return FilterRegion(
        id = id,
        name = name,
        parentId = parentId,
    )
}

fun FilterRegion.toUI(): RegionUI {
    return RegionUI(
        id = id,
        name = name,
        parentId = parentId
    )
}

fun FilterMainData.toUI(): FilterUI {
    return FilterUI(
        country = country.toUI().name,
        region = region.toUI().name,
        industry = industry.toUI().name,
        salary = salary.toIntOrNull(),
        onlyWithSalary = isNeedToHideVacancyWithoutSalary,
    )
}
