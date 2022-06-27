package com.example.jaywalking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("TbWtrmscrslkTfcacdarM")
    fun getXmlList(
        @Query("KEY") KEY:String,
        @Query("pIndex") pIndex:Int,
        @Query("pSize") pSize:Int
    ): Call<TbWtrmscrslkTfcacdarMInfo>
}