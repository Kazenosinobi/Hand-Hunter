package ru.practicum.android.diploma.features.vacancy.data.dto

import ru.practicum.android.diploma.features.common.data.network.dto.area.AreaEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.DetailsVacancyEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.DetailsVacancyEntity.EmploymentEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.DetailsVacancyEntity.ExperienceEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.DetailsVacancyEntity.SkillEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.EmployerEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.SalaryEntity
import ru.practicum.android.diploma.features.common.domain.model.Employer
import ru.practicum.android.diploma.features.common.domain.model.Salary
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails

fun DetailsVacancyEntity.toDomain(): VacancyDetails {
    return VacancyDetails(
        id = id,
        title = name,
        salary = salary?.toDomain() ?: Salary.stub,
        employer = employer.toDomain(),
        location = area.toDomain(),
        experience = experience.toDomain(),
        employmentType = employment.toDomain(),
        description = description,
        keySkills = keySkills.map { it.toDomain() },
        vacancyUrl = vacancyUrl,
    )
}

fun SalaryEntity.toDomain(): Salary {
    return Salary(
        from = from ?: 0,
        to = to ?: 0,
        currency = mapCurrencyToDomain(currency),
        isGross = gross ?: false
    )
}

fun mapCurrencyToDomain(currency: String?): Salary.CurrencyEnum {
    return when (currency) {
        "AZN" -> {
            Salary.CurrencyEnum.AZN
        }

        "BYR" -> {
            Salary.CurrencyEnum.BYR
        }

        "EUR" -> {
            Salary.CurrencyEnum.EUR
        }

        "GEL" -> {
            Salary.CurrencyEnum.GEL
        }

        "KGS" -> {
            Salary.CurrencyEnum.KGS
        }

        "KZT" -> {
            Salary.CurrencyEnum.KZT
        }

        "USD" -> {
            Salary.CurrencyEnum.USD
        }

        "UZS" -> {
            Salary.CurrencyEnum.UZS
        }

        else -> {
            Salary.CurrencyEnum.RUR
        }
    }
}

fun EmployerEntity.toDomain(): Employer {
    return Employer(
        logoUrl = logoUrls?.original,
        name = name
    )
}

fun AreaEntity.toDomain(): String {
    return name
}

fun ExperienceEntity.toDomain(): String {
    return name
}

fun EmploymentEntity.toDomain(): String {
    return name
}

fun SkillEntity.toDomain(): String {
    return name
}
