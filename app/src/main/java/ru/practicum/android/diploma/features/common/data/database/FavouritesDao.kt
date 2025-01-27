package ru.practicum.android.diploma.features.common.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Insert(entity = VacancyDbEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVacancyToFavourites(vacancy: VacancyDbEntity)

    @Insert(entity = KeySkillEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addKeySkillsToFavourites(keySkills: KeySkillEntity)

    @Transaction
    suspend fun addToFavourites(vacancy: VacancyDbEntity, keySkills: List<KeySkillEntity>) {
        addVacancyToFavourites(vacancy)
        keySkills.forEach { skill ->
            addKeySkillsToFavourites(KeySkillEntity(vacancyId = skill.vacancyId, keySkill = skill.keySkill))
        }
    }

    @Query("SELECT * FROM favourites_table")
    fun getFavourites(): Flow<List<VacancyDbEntity>>

    @Query("SELECT key_skill FROM key_skill_table WHERE vacancy_id = :vacancyId")
    suspend fun getKeySkills(vacancyId: String): List<String>

    @Query("SELECT * FROM favourites_table WHERE vacancy_id = :vacancyId")
    suspend fun getVacancy(vacancyId: String): VacancyDbEntity

    @Transaction
    suspend fun getFavouriteVacancy(vacancyId: String): Pair<VacancyDbEntity, List<String>> {
        return Pair(getVacancy(vacancyId), getKeySkills(vacancyId))
    }
}
