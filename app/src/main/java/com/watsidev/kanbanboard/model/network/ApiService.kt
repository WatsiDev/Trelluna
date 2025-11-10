package com.watsidev.kanbanboard.model.network

import com.watsidev.kanbanboard.model.data.columns.ColumnResponse
import com.watsidev.kanbanboard.model.data.columns.CreateColumnRequest
import com.watsidev.kanbanboard.model.data.columns.NewColumnRequest
import com.watsidev.kanbanboard.model.data.columns.UpdateColumnRequest
import com.watsidev.kanbanboard.model.data.login.LoginRequest
import com.watsidev.kanbanboard.model.data.login.LoginResponse
import com.watsidev.kanbanboard.model.data.register.NewUserRequest
import com.watsidev.kanbanboard.model.data.register.UserCreatedResponse
import com.watsidev.kanbanboard.model.data.tasks.NewTaskRequest
import com.watsidev.kanbanboard.model.data.tasks.TaskResponse
import com.watsidev.kanbanboard.model.data.columns.Column as NewColumn
import com.watsidev.kanbanboard.model.data.columns.MessageResponse as NewMessageResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

//const val BASE_URL = "http://192.168.100.4:3000" // Cambia esto por la IP de tu servidor
const val BASE_URL = "https://trelluna-api.onrender.com"
//const val BASE_URL = "http://10.0.2.2:3000"

interface ApiService {

    @GET("/")
    suspend fun checkApiStatus(): String

    @POST("/api/users")
    fun createUser(@Body newUser: NewUserRequest): Call<UserCreatedResponse>

    @POST("api/users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

//    @POST("api/columns")
//    fun createColumn(@Body newColumn: NewColumnRequest): Call<ColumnResponse>

//    @GET("/api/columns")
//    fun getColumns(): Call<List<ColumnResponse>>

    @POST("/api/tasks")
    fun createTask(@Body newTask: NewTaskRequest): Call<TaskResponse>

    @GET("/api/tasks")
    fun getTasksByColumn(@Query("column_id") columnId: Int): Call<List<TaskResponse>>

    /* ------- PROJECTS API ------- */

    @POST("api/projects")
    fun createProject(@Body project: CreateProjectRequest): Call<Project> // Sin suspend

    @GET("api/projects")
    fun getProjects(): Call<List<Project>> // Sin suspend

    @GET("api/projects/{id}")
    fun getProjectById(@Path("id") id: Int): Call<Project> // Sin suspend

    @PUT("api/projects/{id}")
    fun updateProject(
        @Path("id") id: Int,
        @Body updates: UpdateProjectRequest
    ): Call<Project> // Sin suspend

    @DELETE("api/projects/{id}")
    fun deleteProject(@Path("id") id: Int): Call<MessageResponse> // Sin suspend

    /* ------- MEMBERS API ------- */

    @GET("api/projects/{id}/members")
    fun getProjectMembers(@Path("id") projectId: Int): Call<List<ProjectMember>> // Sin suspend

    @POST("api/projects/{id}/members")
    fun addProjectMember(
        @Path("id") projectId: Int,
        @Body member: AddMemberRequest
    ): Call<ProjectMember> // Sin suspend

    @PATCH("api/projects/{id}/members/{userId}")
    fun updateProjectMemberRole(
        @Path("id") projectId: Int,
        @Path("userId") userId: Int,
        @Body role: UpdateMemberRoleRequest
    ): Call<ProjectMember> // Sin suspend

    @DELETE("api/projects/{id}/members/{userId}")
    fun removeProjectMember(
        @Path("id") projectId: Int,
        @Path("userId") userId: Int
    ): Call<MessageResponse> // Sin suspend

    /* ------- COLUMNS API ------- */

    @GET("api/projects/{id}/columns")
    fun getProjectColumns(@Path("id") projectId: Int): Call<List<Column>> // Sin suspend

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

    /*  NEW COLUMNS API */

    /**
     * POST /api/columns
     * Crea una nueva columna.
     */
    @POST("api/columns")
    fun createColumn(@Body request: CreateColumnRequest): Call<NewColumn>

    /**
     * GET /api/columns
     * Obtiene una lista de todas las columnas.
     * Opcionalmente filtra por project_id si no es nulo.
     */
    @GET("api/columns")
    fun getColumns(@Query("project_id") projectId: Int?): Call<List<NewColumn>>

    /**
     * GET /api/columns/:id
     * Obtiene una columna específica por su ID.
     */
    @GET("api/columns/{id}")
    fun getColumnById(@Path("id") columnId: Int): Call<NewColumn>

    /**
     * PUT /api/columns/:id
     * Actualiza el nombre de una columna.
     */
    @PUT("api/columns/{id}")
    fun updateColumn(
        @Path("id") columnId: Int,
        @Body request: UpdateColumnRequest
    ): Call<NewColumn>

    /**
     * DELETE /api/columns/:id
     * Elimina una columna.
     */
    @DELETE("api/columns/{id}")
    fun deleteColumn(@Path("id") columnId: Int): Call<NewMessageResponse>

}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // usa 10.0.2.2 en emulador Android
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
