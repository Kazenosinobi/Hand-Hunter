package ru.practicum.android.diploma.features.common.data.network.service

import retrofit2.HttpException
import ru.practicum.android.diploma.features.common.data.network.api.HHApi
import ru.practicum.android.diploma.features.common.data.network.dto.area.AreaEntity
import ru.practicum.android.diploma.features.common.data.network.dto.area.CountryEntity
import ru.practicum.android.diploma.features.common.data.network.dto.industry.IndustryEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.VacanciesEntity
import ru.practicum.android.diploma.features.common.data.network.dto.vacancy.details.DetailsVacancyEntity
import ru.practicum.android.diploma.features.common.domain.CustomException
import ru.practicum.android.diploma.features.search.domain.model.QuerySearch
import ru.practicum.android.diploma.utils.NetworkChecker
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

interface NetworkClient {

    suspend fun getVacanciesList(
        querySearch: QuerySearch
    ): Result<VacanciesEntity>

    suspend fun getVacancyById(
        id: String,
        params: Map<String, String> = mapOf()
    ): Result<DetailsVacancyEntity>

    suspend fun getCountriesList(params: Map<String, String>): Result<List<CountryEntity>>

    suspend fun getAllAreasList(params: Map<String, String>): Result<List<AreaEntity>>
    suspend fun getAllAreasByIdList(
        countryId: String,
        params: Map<String, String>
    ): Result<List<AreaEntity>>

    suspend fun getIndustriesList(params: Map<String, String>): Result<List<IndustryEntity>>
}

class NetworkClientImpl(
    private val hhApi: HHApi,
    private val networkChecker: NetworkChecker
) : NetworkClient {
    override suspend fun getVacanciesList(
        querySearch: QuerySearch
    ): Result<VacanciesEntity> {
        return runCatching {
            if (networkChecker.isInternetAvailable()) {
                val vacancies = hhApi.getVacancies(
                    text = querySearch.text ?: "",
                    page = querySearch.page,
                    perPage = querySearch.perPage,
                    params = querySearch.params
                )

                if (vacancies.items.isEmpty()) throw CustomException.EmptyError

                vacancies
            } else {
                throw CustomException.NetworkError
            }
        }.recoverCatching {
            resolveError(it)
        }
    }

    override suspend fun getVacancyById(id: String, params: Map<String, String>): Result<DetailsVacancyEntity> {
        return runCatching {
            if (networkChecker.isInternetAvailable()) {
                hhApi.getVacancyDetailsById(id, params)
            } else {
                throw CustomException.NetworkError
            }
        }.recoverCatching {
            resolveError(it)
        }
    }

    override suspend fun getCountriesList(params: Map<String, String>): Result<List<CountryEntity>> {
        return runCatching {
            if (networkChecker.isInternetAvailable()) {
                hhApi.getCountriesList(params)
            } else {
                throw CustomException.NetworkError
            }
        }.recoverCatching {
            resolveError(it)
        }
    }

    override suspend fun getAllAreasList(params: Map<String, String>): Result<List<AreaEntity>> {
        return runCatching {
            if (networkChecker.isInternetAvailable()) {
                hhApi.getAllAreasList(params)
            } else {
                throw CustomException.NetworkError
            }
        }.recoverCatching {
            resolveError(it)
        }
    }

    override suspend fun getAllAreasByIdList(countryId: String, params: Map<String, String>): Result<List<AreaEntity>> {
        return runCatching {
            if (networkChecker.isInternetAvailable()) {
                hhApi.getAllAreasByIdList(countryId, params).areas ?: throw CustomException.NetworkError
            } else {
                throw CustomException.NetworkError
            }
        }.recoverCatching {
            resolveError(it)
        }
    }

    override suspend fun getIndustriesList(params: Map<String, String>): Result<List<IndustryEntity>> {
        return runCatching {
            if (networkChecker.isInternetAvailable()) {
                hhApi.getAllIndustriesList(params)
            } else {
                throw CustomException.NetworkError
            }
        }.recoverCatching { error ->
            resolveError(error)
        }
    }

    private fun resolveError(error: Throwable): Nothing {
        throw when (error) {
            is CustomException.NetworkError -> error
            is CustomException.EmptyError -> error
            is CancellationException -> error
            is IOException -> CustomException.NetworkError
            is HttpException -> {
                if (error.code() in START_OF_ERROR_RANGE..END_OF_ERROR_RANGE) {
                    CustomException.RequestError(error.code())
                } else {
                    CustomException.ServerError
                }
            }

            else -> CustomException.ServerError
        }
    }

    companion object {
        private const val START_OF_ERROR_RANGE = 400
        private const val END_OF_ERROR_RANGE = 499
    }
}
