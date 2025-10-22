package com.watsidev.kanbanboard.ui.screens.projects

import androidx.lifecycle.ViewModel
import com.watsidev.kanbanboard.model.network.AddMemberRequest
import com.watsidev.kanbanboard.model.network.Column
import com.watsidev.kanbanboard.model.network.CreateProjectRequest
import com.watsidev.kanbanboard.model.network.MessageResponse
import com.watsidev.kanbanboard.model.network.Project
import com.watsidev.kanbanboard.model.network.ProjectMember
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.network.UpdateMemberRoleRequest
import com.watsidev.kanbanboard.model.network.UpdateProjectRequest
//import com.watsidev.kanbanboard.model.network.RetrofitInstance // Asumo que tienes esta clase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// --- ESTADOS PARA LA UI ---

/**
 * Eventos de una sola vez para la UI (ej. mostrar Snackbar o navegar)
 */
sealed class ProjectEvent {
    data class ProjectCreated(val project: Project) : ProjectEvent()
    data class ProjectUpdated(val project: Project) : ProjectEvent()
    object ProjectDeleted : ProjectEvent()
    data class MemberAdded(val member: ProjectMember) : ProjectEvent()
    data class MemberUpdated(val member: ProjectMember) : ProjectEvent()
    object MemberRemoved : ProjectEvent()
}

/**
 * Estado completo de la UI para las pantallas de Proyectos
 */
data class ProjectUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Datos para listas y detalles
    val projectList: List<Project> = emptyList(),
    val selectedProject: Project? = null,
    val selectedProjectMembers: List<ProjectMember> = emptyList(),
    val selectedProjectColumns: List<Column> = emptyList(),

    // Estado para formularios de Crear/Editar
    val formProjectName: String = "",
    val formProjectDescription: String = "",
    val formError: String? = null,

    // Evento para notificar a la UI (ej. navegar o mostrar snackbar)
    val lastEvent: ProjectEvent? = null
)


// --- VIEWMODEL ---

class ProjectViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectUiState())
    val uiState: StateFlow<ProjectUiState> = _uiState

    // Asumo que tu RetrofitInstance expone la API de proyectos así
    // NOTA: Si sigues usando "RetrofitInstance.api", reemplaza "projectApi" por "api"
    private val api = RetrofitInstance.api


    // --- Handlers para Forms ---

    fun onProjectNameChange(name: String) {
        _uiState.value = _uiState.value.copy(formProjectName = name, formError = null)
    }

    fun onProjectDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(formProjectDescription = description, formError = null)
    }

    /**
     * Carga los datos de un proyecto existente en el formulario para editarlo
     */
    fun prepareEditForm(project: Project) {
        _uiState.value = _uiState.value.copy(
            formProjectName = project.name,
            formProjectDescription = project.description ?: ""
        )
    }

    /**
     * Limpia los campos del formulario
     */
    fun clearProjectForm() {
        _uiState.value = _uiState.value.copy(
            formProjectName = "",
            formProjectDescription = "",
            formError = null
        )
    }

    /**
     * Marca el evento como consumido para que no se dispare de nuevo
     */
    fun eventConsumed() {
        _uiState.value = _uiState.value.copy(lastEvent = null)
    }


    // --- LÓGICA DE API (PROJECTS) ---

    /**
     * GET /api/projects
     * Obtiene la lista de todos los proyectos
     */
    fun loadProjects() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        api.getProjects().enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        projectList = response.body()!!
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar proyectos"
                    )
                }
            }
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }

    /**
     * GET /api/projects/:id
     * Obtiene los detalles de un proyecto y, si tiene éxito,
     * carga también sus miembros y columnas.
     */
    fun loadProjectDetails(id: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        api.getProjectById(id).enqueue(object : Callback<Project> {
            override fun onResponse(call: Call<Project>, response: Response<Project>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, // Podría seguir cargando miembros/columnas
                        selectedProject = response.body()!!
                    )
                    // Cargar datos relacionados
                    loadProjectMembers(id)
                    loadProjectColumns(id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Proyecto no encontrado"
                    )
                }
            }
            override fun onFailure(call: Call<Project>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }

    /**
     * POST /api/projects
     * Crea un nuevo proyecto usando los datos del formulario
     */
    fun createProject() {
        val state = _uiState.value
        if (state.formProjectName.isBlank()) {
            _uiState.value = state.copy(formError = "El nombre es requerido")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null, formError = null)

        val request = CreateProjectRequest(
            name = state.formProjectName,
            description = state.formProjectDescription.ifBlank { null }
            // created_by: Deberías obtenerlo de tu sesión de usuario
        )

        api.createProject(request).enqueue(object : Callback<Project> {
            override fun onResponse(call: Call<Project>, response: Response<Project>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastEvent = ProjectEvent.ProjectCreated(response.body()!!)
                    )
                    clearProjectForm()
                    loadProjects() // Refrescar la lista
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al crear proyecto"
                    )
                }
            }
            override fun onFailure(call: Call<Project>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }

    /**
     * PUT /api/projects/:id
     * Actualiza un proyecto usando los datos del formulario
     */
    fun updateProject(id: Int) {
        val state = _uiState.value
        if (state.formProjectName.isBlank()) {
            _uiState.value = state.copy(formError = "El nombre es requerido")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null, formError = null)

        val request = UpdateProjectRequest(
            name = state.formProjectName,
            description = state.formProjectDescription
        )

        api.updateProject(id, request).enqueue(object : Callback<Project> {
            override fun onResponse(call: Call<Project>, response: Response<Project>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedProject = response.body()!!, // Actualiza el proyecto seleccionado
                        lastEvent = ProjectEvent.ProjectUpdated(response.body()!!)
                    )
                    loadProjects() // Refrescar la lista
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar proyecto"
                    )
                }
            }
            override fun onFailure(call: Call<Project>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }

    /**
     * DELETE /api/projects/:id
     * Elimina un proyecto
     */
    fun deleteProject(id: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        api.deleteProject(id).enqueue(object: Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastEvent = ProjectEvent.ProjectDeleted,
                        selectedProject = null // Limpiar selección
                    )
                    loadProjects() // Refrescar lista
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error al eliminar")
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error de red: ${t.message}")
            }
        })
    }


    // --- LÓGICA DE API (MEMBERS & COLUMNS) ---

    /**
     * GET /api/projects/:id/members
     */
    fun loadProjectMembers(projectId: Int) {
        // No ponemos isLoading, ya que es una carga secundaria
        api.getProjectMembers(projectId).enqueue(object : Callback<List<ProjectMember>> {
            override fun onResponse(call: Call<List<ProjectMember>>, response: Response<List<ProjectMember>>) {
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        selectedProjectMembers = response.body() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(errorMessage = "Error al cargar miembros")
                }
            }
            override fun onFailure(call: Call<List<ProjectMember>>, t: Throwable) {
                _uiState.value = _uiState.value.copy(errorMessage = "Error de red (miembros): ${t.message}")
            }
        })
    }

    /**
     * GET /api/projects/:id/columns
     */
    fun loadProjectColumns(projectId: Int) {
        api.getProjectColumns(projectId).enqueue(object : Callback<List<Column>> {
            override fun onResponse(call: Call<List<Column>>, response: Response<List<Column>>) {
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        selectedProjectColumns = response.body() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(errorMessage = "Error al cargar columnas")
                }
            }
            override fun onFailure(call: Call<List<Column>>, t: Throwable) {
                _uiState.value = _uiState.value.copy(errorMessage = "Error de red (columnas): ${t.message}")
            }
        })
    }

    /**
     * POST /api/projects/:id/members
     */
    fun addProjectMember(projectId: Int, userId: Int, role: String = "member") {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        val request = AddMemberRequest(userId = userId, role = role)

        api.addProjectMember(projectId, request).enqueue(object : Callback<ProjectMember> {
            override fun onResponse(call: Call<ProjectMember>, response: Response<ProjectMember>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastEvent = ProjectEvent.MemberAdded(response.body()!!)
                    )
                    loadProjectMembers(projectId) // Refrescar lista de miembros
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al agregar miembro"
                    )
                }
            }
            override fun onFailure(call: Call<ProjectMember>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }

    /**
     * PATCH /api/projects/:id/members/:userId
     */
    fun updateProjectMemberRole(projectId: Int, userId: Int, newRole: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        val request = UpdateMemberRoleRequest(role = newRole)

        api.updateProjectMemberRole(projectId, userId, request).enqueue(object : Callback<ProjectMember> {
            override fun onResponse(call: Call<ProjectMember>, response: Response<ProjectMember>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastEvent = ProjectEvent.MemberUpdated(response.body()!!)
                    )
                    loadProjectMembers(projectId) // Refrescar lista de miembros
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar rol"
                    )
                }
            }
            override fun onFailure(call: Call<ProjectMember>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }

    /**
     * DELETE /api/projects/:id/members/:userId
     */
    fun removeProjectMember(projectId: Int, userId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        api.removeProjectMember(projectId, userId).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastEvent = ProjectEvent.MemberRemoved
                    )
                    loadProjectMembers(projectId) // Refrescar lista de miembros
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar miembro"
                    )
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de red: ${t.message}"
                )
            }
        })
    }
}
