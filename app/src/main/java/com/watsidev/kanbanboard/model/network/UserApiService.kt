package com.watsidev.kanbanboard.model.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

// --- Modelos de Datos (Data Classes) ---

/**
 * Modelo de usuario base.
 * @param createdAt es opcional porque /login y /register no lo devuelven.
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("created_at")
    val createdAt: String? = null
)

/**
 * Respuesta para Login y Register
 */
data class AuthResponse(
    val message: String,
    val token: String,
    val user: User
)

/**
 * Respuesta genérica para DELETE, PATCH, etc.
 */
data class MessageResponseUser(
    val message: String
)

/**
 * Respuesta para la creación de usuario (Admin)
 */
data class CreateUserResponse(
    val message: String,
    val user: User
)

// --- Modelos de Petición (Request Bodies) ---

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Para POST /api/users (Admin)
 */
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

/**
 * Para PUT /api/users/:id
 */
data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val role: String? = null
)

/**
 * Para PATCH /api/users/:id/password
 */
data class UpdatePasswordRequest(
    val password: String
)


// --- Interfaz de Retrofit para la API de Usuarios ---

/* interface UserApiService {

    /**
     * POST: /api/users/login
     */
    @POST("api/users/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    /**
     * POST: /api/users/register
     */
    @POST("api/users/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    /**
     * GET: /api/users/auth/me
     * (Requiere token de autorización)
     */
    @GET("api/users/auth/me")
    fun getMyProfile(@Header("Authorization") token: String): Call<User>

    /**
     * GET: /api/users
     * (Requiere token de autorización)
     * Nota: Tu API no especificaba 'auth' aquí, pero listar usuarios
     * debería ser protegido. Asumo que requiere token.
     */
    @GET("api/users")
    fun listUsers(@Header("Authorization") token: String): Call<List<User>>

    /**
     * POST: /api/users
     * (Crear usuario como Admin, requiere token)
     */
    @POST("api/users")
    fun createUser(
        @Header("Authorization") token: String,
        @Body request: CreateUserRequest
    ): Call<CreateUserResponse>

    /**
     * GET: /api/users/:id
     * (Requiere token de autorización)
     */
    @GET("api/users/{id}")
    fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Call<User>

    /**
     * PUT: /api/users/:id
     * (Requiere token de autorización)
     */
    @PUT("api/users/{id}")
    fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UpdateUserRequest
    ): Call<User>

    /**
     * PATCH: /api/users/:id/password
     * (Requiere token de autorización)
     */
    @PATCH("api/users/{id}/password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body request: UpdatePasswordRequest
    ): Call<MessageResponse>

    /**
     * DELETE: /api/users/:id
     * (Requiere token de autorización)
     */
    @DELETE("api/users/{id}")
    fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Call<MessageResponse>

}
*/