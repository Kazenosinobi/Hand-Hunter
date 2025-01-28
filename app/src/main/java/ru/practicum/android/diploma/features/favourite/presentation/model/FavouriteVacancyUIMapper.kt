package ru.practicum.android.diploma.features.favourite.presentation.model

import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.common.presentation.ResourceProvider
import ru.practicum.android.diploma.features.common.presentation.models.VacancySearchUI
import ru.practicum.android.diploma.utils.getFormatSalary
import ru.practicum.android.diploma.utils.getProfession

fun VacancyDetails.toUi(resourceProvider: ResourceProvider): VacancySearchUI {
    return VacancySearchUI(
        id = id,
        formatedProfession = getProfession(title, location),
        formatedSalary = getFormatSalary(
            salary.from,
            salary.to,
            salary.currency.symbol,
            resourceProvider
        ),
        employerLogoUrl = employer.logoUrl ?: "",
        employerName = employer.name
    )
}
