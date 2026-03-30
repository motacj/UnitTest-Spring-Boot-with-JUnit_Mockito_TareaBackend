package es.atomsusej.tareaBackend.security

// Se encarga de recoger lo que tu envias al hacer el login
data class AuthResquest(
    val username: String,
    val password: String
)