package com.watsidev.kanbanboard.model.network

import com.watsidev.kanbanboard.model.data.columns.ColumnResponse
import com.watsidev.kanbanboard.model.data.columns.NewColumnRequest
import com.watsidev.kanbanboard.model.data.login.LoginRequest
import com.watsidev.kanbanboard.model.data.login.LoginResponse
import com.watsidev.kanbanboard.model.data.register.NewUserRequest
import com.watsidev.kanbanboard.model.data.register.UserCreatedResponse
import com.watsidev.kanbanboard.model.data.tasks.NewTaskRequest
import com.watsidev.kanbanboard.model.data.tasks.TaskResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "http://192.168.100.4:3000" // Cambia esto por la IP de tu servidor
//const val BASE_URL = "https://kanban-api-production-3916.up.railway.app/"
interface ApiService {
    @POST("/api/users")
    fun createUser(@Body newUser: NewUserRequest): Call<UserCreatedResponse>

    @POST("api/users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/columns")
    fun createColumn(@Body newColumn: NewColumnRequest): Call<ColumnResponse>

    @GET("/api/columns")
    fun getColumns(): Call<List<ColumnResponse>>

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
