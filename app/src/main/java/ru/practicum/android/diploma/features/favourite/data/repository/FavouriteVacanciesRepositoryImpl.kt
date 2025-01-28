package ru.practicum.android.diploma.features.favourite.data.repository

import android.database.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.features.common.data.database.FavouritesDao
import ru.practicum.android.diploma.features.common.data.database.KeySkillEntity
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.favourite.data.dto.toDb
import ru.practicum.android.diploma.features.favourite.data.dto.toDomain
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesRepository
import ru.practicum.android.diploma.features.favourite.presentation.model.FavouriteVacanciesState

class FavouriteVacanciesRepositoryImpl(
    private val favouritesDao: FavouritesDao,
) : FavouriteVacanciesRepository {

    override suspend fun addToFavourites(vacancy: VacancyDetails) {
        val keySkills = vacancy.keySkills.map { skill ->
            createKeySkillEntity(vacancy.id, skill)
        }
        favouritesDao.addToFavourites(
            vacancy.toDb(),
            keySkills
        )
    }

    override fun getFavourites(): Flow<List<VacancyDetails>> {
        try {
            return favouritesDao.getFavourites()
                .map { vacancies ->
                    vacancies.map { it.toDomain() }.reversed()
                }
        } catch (e: SQLiteException) {
            FavouriteVacanciesState.Error(e.message.toString())
        }
        return emptyFlow()
    }

    override suspend fun deleteFavouriteVacancy(vacancyId: String) {
        favouritesDao.deleteFavouriteVacancy(vacancyId)
    }

    private fun createKeySkillEntity(vacancyId: String, skill: String): KeySkillEntity {
        return KeySkillEntity(
            vacancyId = vacancyId,
            keySkill = skill,
        )
    }

}

