package com.watsidev.kanbanboard.model.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// --- MODELOS DE DATOS (DATA CLASSES) ---

/**5
 * Modelo para la respuesta de un Proyecto.
 * (Asumiendo que la BBDD devuelve 'created_at' y 'updated_at' como Strings)
 */
data class Project(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("created_by") val createdBy: Int?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

/**
 * Modelo para un Miembro de Proyecto (combinando 'project_members' y 'users')
 */
data class ProjectMember(
    @SerializedName("project_id") val projectId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("role") val role: String,
    @SerializedName("added_at") val addedAt: String,
    @SerializedName("name") val name: String, // Del JOIN con 'users'
    @SerializedName("email") val email: String // Del JOIN con 'users'
)

/**
 * Modelo para las Columnas de un proyecto
 */
data class Column(
    @SerializedName("id") val id: Int,
    @SerializedName("project_id") val projectId: Int,
    @SerializedName("name") val name: String,
    //@SerializedName("position") val position: Int,
    @SerializedName("created_at") val createdAt: String
)

/**
 * Modelo genérico para respuestas de mensajes (ej. "Proyecto eliminado")
 */
data class MessageResponse(
    @SerializedName("message") val message: String
)

// --- MODELOS PARA REQUEST BODIES (PETICIONES) ---

/**
 * Body para POST /api/projects
 */
data class CreateProjectRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("created_by") val createdBy: Int? = null
)

/**
 * Body para PUT /api/projects/:id
 * Permite actualización parcial
 */
data class UpdateProjectRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null
)

/**
 * Body para POST /api/projects/:id/members
 */
data class AddMemberRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("role") val role: String? = "member"
)

/**
 * Body para PATCH /api/projects/:id/members/:userId
 */
data class UpdateMemberRoleRequest(
    @SerializedName("role") val role: String
)


// --- INTERFAZ DE RETROFIT ---

interface ProjectApiService {

    /* ------- PROJECTS API ------- */

    /**
     * POST /api/projects
     * Crea un nuevo proyecto
     */
    @POST("api/projects")
    suspend fun createProject(@Body project: CreateProjectRequest): Project

    /**
     * GET /api/projects
     * Lista todos los proyectos
     */
    @GET("api/projects")
    suspend fun getProjects(): List<Project>

    /**
     * GET /api/projects/:id
     * Obtiene un proyecto por su id
     */
    @GET("api/projects/{id}")
    suspend fun getProjectById(@Path("id") id: Int): Project

    /**
     * PUT /api/projects/:id
     * Actualiza (parcial o total) un proyecto
     */
    @PUT("api/projects/{id}")
    suspend fun updateProject(
        @Path("id") id: Int,
        @Body updates: UpdateProjectRequest
    ): Project

    /**
     * DELETE /api/projects/:id
     * Elimina un proyecto
     */
    @DELETE("api/projects/{id}")
    suspend fun deleteProject(@Path("id") id: Int): Response<MessageResponse>

    /* ------- MEMBERS API ------- */

    /**
     * GET /api/projects/:id/members
     * Lista los miembros (colaboradores) de un proyecto
     */
    @GET("api/projects/{id}/members")
    suspend fun getProjectMembers(@Path("id") projectId: Int): List<ProjectMember>

    /**
     * POST /api/projects/:id/members
     * Agrega o actualiza un miembro del proyecto
     */
    @POST("api/projects/{id}/members")
    suspend fun addProjectMember(
        @Path("id") projectId: Int,
        @Body member: AddMemberRequest
    ): ProjectMember

    /**
     * PATCH /api/projects/:id/members/:userId
     * Cambia el rol de un miembro del proyecto
     */
    @PATCH("api/projects/{id}/members/{userId}")
    suspend fun updateProjectMemberRole(
        @Path("id") projectId: Int,
        @Path("userId") userId: Int,
        @Body role: UpdateMemberRoleRequest
    ): ProjectMember

    /**
     * DELETE /api/projects/:id/members/:userId
     * Elimina un miembro del proyecto
     */
    @DELETE("api/projects/{id}/members/{userId}")
    suspend fun removeProjectMember(
        @Path("id") projectId: Int,
        @Path("userId") userId: Int
    ): Response<MessageResponse>

    /* ------- COLUMNS API ------- */

    /**
     * GET /api/projects/:id/columns
     * (Opcional) Lista las columnas de un proyecto
     */
    @GET("api/projects/{id}/columns")
    suspend fun getProjectColumns(@Path("id") projectId: Int): List<Column>

}
