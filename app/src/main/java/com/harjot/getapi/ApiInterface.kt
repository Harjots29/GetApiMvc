package com.harjot.getapi

import retrofit2.http.GET

interface ApiInterface {
    @GET("/api/users")
    fun getData():retrofit2.Call<ResponseModel>
}