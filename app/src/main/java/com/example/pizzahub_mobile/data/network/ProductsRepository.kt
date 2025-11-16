package com.example.pizzahub_mobile.data.network

import android.content.Context
import com.example.pizzahub_mobile.data.models.Product
import com.example.pizzahub_mobile.data.models.toProduct

class ProductsRepository(private val context: Context) {
    private val api: ProductsApi = RetrofitInstance.create(context).create(ProductsApi::class.java)

    suspend fun fetchProducts(): Result<List<Product>> =
            try {
                val resp = api.getProducts()
                if (resp.isSuccessful) {
                    val body = resp.body() ?: emptyList()
                    Result.success(body.map { it.toProduct() })
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
}
