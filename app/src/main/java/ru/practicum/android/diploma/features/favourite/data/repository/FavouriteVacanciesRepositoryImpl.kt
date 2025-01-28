package ru.practicum.android.diploma.features.favourite.data.repository

import android.database.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.features.common.data.database.AppDatabase
import ru.practicum.android.diploma.features.common.data.database.KeySkillEntity
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.favourite.data.dto.toDb
import ru.practicum.android.diploma.features.favourite.data.dto.toDomain
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesRepository
import ru.practicum.android.diploma.features.favourite.presentation.model.FavouriteVacanciesState

class FavouriteVacanciesRepositoryImpl(
    private val appDatabase: AppDatabase,
) : FavouriteVacanciesRepository {

    override suspend fun addToFavourites(vacancy: VacancyDetails) {
        val keySkills = vacancy.keySkills.map { skill ->
            createKeySkillEntity(vacancy.id, skill)
        }
        appDatabase.favouritesDao().addToFavourites(
            vacancy.toDb(),
            keySkills
        )
    }

    override fun getFavourites(): Flow<List<VacancyDetails>> {
        try {
            return appDatabase.favouritesDao().getFavourites()
                .map { vacancies ->
                    vacancies.map { it.toDomain() }.reversed()
                }
        }catch (e: SQLiteException) {
            FavouriteVacanciesState.Error(e.message.toString())
        }
        return emptyFlow()
    }

    private fun createKeySkillEntity(vacancyId: String, skill: String): KeySkillEntity {
        return KeySkillEntity(
            vacancyId = vacancyId,
            keySkill = skill,
        )
    }

}

