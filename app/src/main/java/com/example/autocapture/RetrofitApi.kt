package com.example.autocapture

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitApi {

    @POST("uploadImage")
    fun uploadImage(@Body dataModal: DataModal): Call<UploadResponse>

    @GET("GetImages")
    fun fetchImage(): Call<ImageResponse>
}

class DataModal internal constructor(
    val image: String,
    val date: String
)

