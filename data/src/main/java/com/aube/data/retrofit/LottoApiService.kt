package com.aube.data.retrofit

import com.aube.data.model.response.LottoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LottoApiService {
    @GET("common.do?method=getLottoNumber")
    suspend fun getLottoResult(@Query("drwNo") round: Int): LottoResponse
}
