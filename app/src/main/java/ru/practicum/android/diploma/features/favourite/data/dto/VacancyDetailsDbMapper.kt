package ru.practicum.android.diploma.features.favourite.data.dto

import ru.practicum.android.diploma.features.common.data.database.VacancyDbEntity
import ru.practicum.android.diploma.features.common.domain.model.Employer
import ru.practicum.android.diploma.features.common.domain.model.Salary
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.vacancy.data.dto.mapCurrencyToDomain

fun VacancyDetails.toDb(): VacancyDbEntity {
    return VacancyDbEntity(
        vacancyId = id,
        title = title,
        salaryFrom = salary.from,
        salaryTo = salary.to,
        currencyAbbr = salary.currency.name,
        isGross = salary.isGross,
        employerName = employer.name,
        employerLogoUrl = employer.logoUrl,
        location = location,
        experience = experience,
        employmentForm = employmentType,
        description = description,
        vacancyUrl = vacancyUrl
    )
}

fun VacancyDbEntity.toDomain(skills: List<String> = emptyList()): VacancyDetails {
    return VacancyDetails(
        id = vacancyId,
        title = title,
        salary = Salary(
            from = salaryFrom ?: 0,
            to = salaryTo ?: 0,
            currency = mapCurrencyToDomain(currencyAbbr),
            isGross = isGross
        ),
        employer = Employer(
            name = employerName,
            logoUrl = employerLogoUrl
        ),
        location = location,
        experience = experience,
        employmentType = employmentForm,
        description = description,
        keySkills = skills,
        vacancyUrl = vacancyUrl,
        isFavourite = true
    )
}
