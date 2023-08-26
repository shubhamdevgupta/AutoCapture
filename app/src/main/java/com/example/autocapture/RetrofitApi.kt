package com.example.autocapture

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST


interface RetrofitApi {

    @POST("uploadImage")
    fun uploadImage(@Body dataModal: DataModal): Call<String?>?

    @GET("GetImages")
    fun fetchImage():Call<ImageResponse>
}

class DataModal {
    var image: String? = null
    var date: String? = null
}


