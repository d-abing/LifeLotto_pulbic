package com.aube.domain.repository

import com.aube.domain.model.LottoResult

interface LottoRepository {
    suspend fun getLottoResult(round: Int): LottoResult
}
