package com.watsidev.kanbanboard.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watsidev.kanbanboard.model.network.ApiService
import com.watsidev.kanbanboard.model.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface ServerState {
    /** El estado inicial, mientras la splash screen está visible */
    object InitialLoading : ServerState // <-- Renombrado

    /** El servidor está dormido y lo estamos "despertando" (retry) */
    object WakingUp : ServerState

    /** El servidor respondió y está listo para recibir peticiones */
    object Ready : ServerState

    /** Error: No hay conexión a internet */
    object NetworkError : ServerState // <-- NUEVO

    /** Error: El servidor falló (timeout final o error 500) */
    object ServerError : ServerState  // <-- NUEVO
}


class MainViewModel(
    private val loadApi: ApiService = RetrofitInstance.api
) : ViewModel() {

    // 1. Empezamos en el nuevo estado InitialLoading
    private val _serverState = MutableStateFlow<ServerState>(ServerState.InitialLoading)
    val serverState = _serverState.asStateFlow()

    private val maxRetries = 6
    private val retryDelayMs = 5000L

    init {
        // 2. Llamamos a nuestra lógica de primer intento
        checkFirstAttempt()
    }

    /**
     * Esta función es llamada por el botón "Reintentar" desde la UI.
     */
    fun retryConnection() {
        Log.d("MainViewModel", "Reintento manual solicitado por el usuario.")
        // Ponemos el estado en WakingUp para mostrar el modal de Lottie
        _serverState.value = ServerState.WakingUp
        // Y simplemente volvemos a lanzar el bucle de reintentos
        startRetryLoop()
    }

    /**
     * Intenta conectar solo una vez (para la splash screen).
     */
    private fun checkFirstAttempt() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Intento 1 (Splash Screen)...")
                val response = loadApi.checkApiStatus()
                _serverState.value = ServerState.Ready
                Log.d("MainViewModel", "Servidor listo en el primer intento: $response")

            } catch (e: Exception) {
                // 3. ¡El CATCH inteligente!
                when (e) {
                    // Caso A: El servidor está dormido (Timeout)
                    is SocketTimeoutException -> {
                        Log.w("MainViewModel", "Intento 1 fallido (timeout). El servidor está dormido.")
                        _serverState.value = ServerState.WakingUp
                        startRetryLoop() // Inicia los reintentos en segundo plano
                    }
                    // Caso B: No hay internet
                    is UnknownHostException, is ConnectException -> {
                        Log.e("MainViewModel", "Intento 1 fallido (Sin Red).", e)
                        _serverState.value = ServerState.NetworkError
                    }
                    // Caso C: Otro error (ej. 500, 404, etc.)
                    else -> {
                        Log.e("MainViewModel", "Intento 1 fallido (Error Servidor).", e)
                        _serverState.value = ServerState.ServerError
                    }
                }
            }
        }
    }

    /**
     * Bucle de reintentos que se muestra en el modal.
     */
    private fun startRetryLoop() {
        viewModelScope.launch {
            var currentRetry = 0
            while (currentRetry < maxRetries &&
                (_serverState.value == ServerState.WakingUp || _serverState.value == ServerState.InitialLoading)) {

                currentRetry++
                Log.d("MainViewModel", "Reintento $currentRetry/$maxRetries...")

                try {
                    val response = loadApi.checkApiStatus()
                    _serverState.value = ServerState.Ready
                    Log.d("MainViewModel", "¡Servidor despierto en el reintento $currentRetry!: $response")
                    break // ¡Éxito! Salimos del bucle

                } catch (e: Exception) {
                    // 4. Manejo de errores DENTRO del bucle
                    when (e) {
                        // Si perdemos la red mientras reintentamos
                        is UnknownHostException, is ConnectException -> {
                            Log.e("MainViewModel", "Se perdió la red durante los reintentos.")
                            _serverState.value = ServerState.NetworkError
                            break // Salimos del bucle
                        }
                        // Si es otro timeout, simplemente dejamos que el bucle continúe
                        is SocketTimeoutException -> {
                            Log.w("MainViewModel", "Reintento $currentRetry fallido (timeout).")
                        }
                        // Otro error
                        else -> {
                            Log.e("MainViewModel", "Error de servidor durante reintento.", e)
                            _serverState.value = ServerState.ServerError
                            break // Salimos del bucle
                        }
                    }

                    // 5. Si se acaban los reintentos, es un error de servidor
                    if (currentRetry == maxRetries) {
                        Log.e("MainViewModel", "El servidor no despertó después de $maxRetries intentos.")
                        _serverState.value = ServerState.ServerError
                    }
                }

                // Solo esperamos si no hemos terminado
                if (_serverState.value == ServerState.WakingUp) {
                    delay(retryDelayMs)
                }
            }
        }
    }
}