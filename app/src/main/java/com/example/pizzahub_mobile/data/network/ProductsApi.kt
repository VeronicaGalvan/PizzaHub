package com.example.pizzahub_mobile.data.network

import com.example.pizzahub_mobile.data.models.ProductResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductsApi {
    @GET("api/Productos") suspend fun getProducts(): Response<List<ProductResponse>>
}
