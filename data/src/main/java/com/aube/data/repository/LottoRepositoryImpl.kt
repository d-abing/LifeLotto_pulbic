package com.aube.data.repository

import com.aube.data.model.response.toDomain
import com.aube.data.retrofit.LottoApiService
import com.aube.domain.model.LottoResult
import com.aube.domain.repository.LottoRepository
import javax.inject.Inject

class LottoRepositoryImpl @Inject constructor(
    private val api: LottoApiService
) : LottoRepository {
    override suspend fun getLottoResult(round: Int): LottoResult = api.getLottoResult(round).toDomain()
}

