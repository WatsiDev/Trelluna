package com.watsidev.kanbanboard.model.network

import com.watsidev.kanbanboard.model.data.login.LoginRequest
import com.watsidev.kanbanboard.model.data.login.LoginResponse
import com.watsidev.kanbanboard.model.data.register.NewUserRequest
import com.watsidev.kanbanboard.model.data.register.UserCreatedResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "http://192.168.100.4:3000" // Cambia esto por la IP de tu servidor
interface ApiService {
    @POST("/api/users")
    fun createUser(@Body newUser: NewUserRequest): Call<UserCreatedResponse>

    @POST("api/users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // usa 10.0.2.2 en emulador Android
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
