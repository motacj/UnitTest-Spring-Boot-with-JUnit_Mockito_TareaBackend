package es.atomsusej.tareaBackend.unit.service

import es.atomsusej.tareaBackend.security.JwtService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.junit.jupiter.api.Assertions.assertThrows


class JwtServiceUnitsTest {

    // Aquí creo un secreto suficientemente largo para HS256
    private val secret = "mi_clave_secreta_super_larga_para_jwt_123456789"

    // Aquí indico que el token dure 60 minutos en los tests normales
    private val expirationMinutes = 60L

    // Aquí creo directamente el servicio sin levantar Spring
    private val jwtService = JwtService(secret, expirationMinutes)

    @Test
    fun generateToken_generates_token_correctamente() {
        // Aquí creo un usuario con rol
        val user = User(
            "admin",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        // Aquí genero el token
        val token = jwtService.generateToken(user)

        // Aquí compruebo que el token se ha creado
        assertNotNull(token)
        assertTrue(token.isNotBlank())
    }

    @Test
    fun extractUsername_devuelve_username_correcto() {
        val user = User(
            "admin",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        val token = jwtService.generateToken(user)

        // Aquí extraigo el username del token
        val username = jwtService.extractUsername(token)

        // Aquí compruebo que coincide
        assertEquals("admin", username)
    }

    @Test
    fun extractRoles_devuelve_roles_correctos() {
        val user = User(
            "user",
            "1234",
            listOf(
                SimpleGrantedAuthority("ROLE_USER"),
                SimpleGrantedAuthority("ROLE_TEST")
            )
        )

        val token = jwtService.generateToken(user)

        // Aquí extraigo los roles del token
        val roles = jwtService.extractRoles(token)

        // Aquí compruebo que devuelve los roles correctos
        assertEquals(2, roles.size)
        assertTrue(roles.contains("ROLE_USER"))
        assertTrue(roles.contains("ROLE_TEST"))
    }

    @Test
    fun isTokenValid_devuelve_true_si_token_es_correcto() {
        val user = User(
            "admin",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        val token = jwtService.generateToken(user)

        // Aquí compruebo que el token es válido para ese usuario
        val valido = jwtService.isTokenValid(token, user)

        assertTrue(valido)
    }

    @Test
    fun isTokenValid_devuelve_false_si_el_usuario_no_coincide() {
        val user1 = User(
            "admin",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        val user2 = User(
            "otroUsuario",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        val token = jwtService.generateToken(user1)

        // Aquí compruebo que el token no vale para otro usuario distinto
        val valido = jwtService.isTokenValid(token, user2)

        assertFalse(valido)
    }

    @Test
    fun isTokenValid_lanza_excepcion_si_el_token_esta_expirado() {
        // Aquí creo otro JwtService con expiración negativa para forzar que el token nazca caducado
        val jwtServiceExpirado = JwtService(secret, -1L)

        val user = User(
            "admin",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        val token = jwtServiceExpirado.generateToken(user)

        // Aquí compruebo que al validar un token expirado se lanza una excepción
        assertThrows(io.jsonwebtoken.ExpiredJwtException::class.java) {
            jwtServiceExpirado.isTokenValid(token, user)
        }
    }

    @Test
    fun extractRoles_devuelve_lista_vacia_si_no_hay_roles_validos() {
        val user = User(
            "admin",
            "1234",
            emptyList()
        )

        val token = jwtService.generateToken(user)

        // Aquí extraigo los roles del token
        val roles = jwtService.extractRoles(token)

        // Aquí compruebo que devuelve lista vacía
        assertTrue(roles.isEmpty())
    }
}