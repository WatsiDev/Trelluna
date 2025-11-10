package com.watsidev.kanbanboard.model.session

import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import com.watsidev.kanbanboard.model.network.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SessionManager {

    private var currentToken: String? = null
    private var currentUser: User? = null

    // Asumimos que RetrofitInstance.api te da una instancia de ApiService
    // que incluye tu nuevo endpoint getMyProfile
    private val api: ApiService = RetrofitInstance.api

    /**
     * Llama a esta función DESPUÉS de un login/registro exitoso.
     * Almacena el token y solicita el perfil del usuario.
     *
     * @param token El token JWT recibido del login/registro (sin el prefijo "Bearer ").
     * @param onSessionReady Callback que se ejecuta cuando el token Y el perfil del usuario
     * se han cargado exitosamente.
     * @param onError Callback que se ejecuta si algo falla.
     */
    fun initializeSession(token: String, onSessionReady: () -> Unit, onError: (String) -> Unit) {

        // Formateamos y guardamos el token
        val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        currentToken = formattedToken

        // 2. Llamamos al endpoint /me para obtener los datos del usuario
        api.getMyProfile(formattedToken).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    currentUser = response.body()
                    if (currentUser != null) {
                        onSessionReady() // ¡Éxito! Sesión lista.
                    } else {
                        onError("No se pudo obtener el perfil del usuario.")
                    }
                } else {
                    onError("Error de perfil: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError("Fallo de red al obtener perfil: ${t.message}")
            }
        })
    }

    /**
     * Obtiene el ID del usuario logueado (desde la memoria).
     * @return El ID del usuario como String, o un string vacío si no hay sesión.
     */
    fun getUserId(): String {
        // TODO: Asegúrate de que tu data class 'User' tenga una propiedad 'id'.
        // Si 'id' es un Int, .toString() funcionará bien.
        return currentUser?.id?.toString() ?: ""
    }

    /**
     * Obtiene el token de autorización actual (para otras llamadas de API).
     * @return El token formateado (ej: "Bearer ...") o null si no hay sesión.
     */
    fun getAuthToken(): String? {
        return currentToken
    }

    /**
     * ¡NUEVO! Obtiene el objeto User completo del usuario logueado.
     */
    fun getUser(): User? {
        return currentUser
    }

    /**
     * Limpia la sesión (para el logout).
     */
    fun clearSession() {
        currentToken = null
        currentUser = null
    }

    fun updateUser(body: User) {
        currentUser = body

    }
}