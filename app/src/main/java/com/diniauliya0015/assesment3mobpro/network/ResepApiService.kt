package com.diniauliya0015.assesment3mobpro.network

import com.diniauliya0015.assesment3mobpro.model.Resep
import com.diniauliya0015.assesment3mobpro.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://store.sthresearch.site/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ResepApiService {
    @GET("food_recipe.php")
    suspend fun getResep(
        @Header("Authorization") userId: String
    ): List<Resep>

    @Multipart
    @POST("food_recipe.php")
    suspend fun postResep(
        @Header("Authorization") userId: String,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("langkah") langkah: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @Multipart
    @POST("food_recipe.php")
    suspend fun updateReceipt(
        @Header("Authorization") userId: String,
        @Part("id") id: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("langkah") langkah: RequestBody
    ): OpStatus

    @Multipart
    @POST("food_recipe.php")
    suspend fun updateReceiptWithImage(
        @Header("Authorization") userId: String,
        @Part("id") id: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("langkah") langkah: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("food_recipe.php")
    suspend fun deleteResep(
        @Header("Authorization") userId: String,
        @Query("id") id: String
    ): OpStatus
}

object ResepApi{
    val service: ResepApiService by lazy {
        retrofit.create(ResepApiService::class.java)
    }
    fun getResepUrl(imageId: String):String{
        return "${BASE_URL}image.php?id=$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED}