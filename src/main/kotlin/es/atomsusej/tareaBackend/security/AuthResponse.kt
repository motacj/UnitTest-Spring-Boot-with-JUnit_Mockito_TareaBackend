package es.atomsusej.tareaBackend.security

// Lo que el backend devuelve tras login: el token JWT
data class AuthResponse(
    val token: String
)