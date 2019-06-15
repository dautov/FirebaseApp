package com.example.bilal.firebaseapp.activity

import com.example.bilal.firebaseapp.model.FCMModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMINterface {

    @POST("send")
    fun bildirimGonder(
        @HeaderMap headers: Map<String,String>,
        @Body bildirimMesaj: FCMModel
    ): Call<Response<FCMModel>>
}