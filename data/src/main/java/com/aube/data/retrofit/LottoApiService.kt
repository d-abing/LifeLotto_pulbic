package com.aube.data.retrofit

import androidx.annotation.Keep
import com.aube.data.model.response.LottoResponse
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface LottoApiService {
    @GET("common.do?method=getLottoNumber")
    suspend fun getLottoResult(@Query("drwNo") round: Int): LottoResponse
}
