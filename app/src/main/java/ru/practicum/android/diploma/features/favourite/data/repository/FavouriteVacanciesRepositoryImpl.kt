package ru.practicum.android.diploma.features.favourite.data.repository

import android.database.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.features.common.data.database.FavouritesDao
import ru.practicum.android.diploma.features.common.data.database.KeySkillEntity
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.common.domain.model.VacancyDetails
import ru.practicum.android.diploma.features.favourite.data.dto.toDb
import ru.practicum.android.diploma.features.favourite.data.dto.toDomain
import ru.practicum.android.diploma.features.favourite.domain.api.FavouriteVacanciesRepository

class FavouriteVacanciesRepositoryImpl(
    private val favouritesDao: FavouritesDao,
) : FavouriteVacanciesRepository {

    override suspend fun addToFavourites(vacancy: VacancyDetails): Result<Unit> {
        return runCatching {
            val keySkills = vacancy.keySkills.map { skill ->
                createKeySkillEntity(vacancy.id, skill)
            }
            favouritesDao.addToFavourites(
                vacancy.toDb(),
                keySkills
            )
        }.recoverCatching {
            throw CustomException.UpdateDatabaseError
        }
    }

    override fun getFavourites(): Flow<List<VacancyDetails>> {
        try {
            return favouritesDao.getFavourites()
                .map { vacancies ->
                    vacancies.map { it.toDomain() }.reversed()
                }.catch {
                    throw CustomException.DatabaseGettingError
                }
        } catch (e: SQLiteException) {
            println(e)
            throw CustomException.DatabaseGettingError
        }

    }

    override suspend fun deleteFavouriteVacancy(vacancyId: String): Result<Unit> {
        return kotlin.runCatching {
            favouritesDao.deleteFavouriteVacancy(vacancyId)
        }.recoverCatching {
            throw CustomException.UpdateDatabaseError
        }
    }

    override suspend fun deleteFavourites(): Result<Unit> {
        return kotlin.runCatching {
            favouritesDao.deleteAllData()
        }.recoverCatching {
            throw CustomException.UpdateDatabaseError
        }
    }

    private fun createKeySkillEntity(vacancyId: String, skill: String): KeySkillEntity {
        return KeySkillEntity(
            vacancyId = vacancyId,
            keySkill = skill,
        )
    }

}

