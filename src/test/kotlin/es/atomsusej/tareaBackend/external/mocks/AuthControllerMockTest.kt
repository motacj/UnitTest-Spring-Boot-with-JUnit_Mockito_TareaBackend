package es.atomsusej.tareaBackend.unit.controller

import es.atomsusej.tareaBackend.security.AuthResquest
import es.atomsusej.tareaBackend.security.JwtService
import es.atomsusej.tareaBackend.web.AuthController
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService

@ExtendWith(MockitoExtension::class)
class AuthControllerMockTest {

    @Mock
    lateinit var authManager: AuthenticationManager

    @Mock
    lateinit var userDetailsService: UserDetailsService

    @Mock
    lateinit var jwtService: JwtService

    @Mock
    lateinit var authentication: Authentication

    @Test
    fun login_correcto_devuelve_token_mock() {
        val request = AuthResquest(
            username = "admin",
            password = "1234"
        )

        val user = User(
            "admin",
            "1234",
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )

        `when`(
            authManager.authenticate(
                UsernamePasswordAuthenticationToken("admin", "1234")
            )
        ).thenReturn(authentication)

        `when`(userDetailsService.loadUserByUsername("admin")).thenReturn(user)
        `when`(jwtService.generateToken(user)).thenReturn("token-falso-123")

        val authController = AuthController(authManager, userDetailsService, jwtService)

        val response = authController.login(request)

        assertEquals("token-falso-123", response.token)

        verify(authManager, times(1)).authenticate(
            UsernamePasswordAuthenticationToken("admin", "1234")
        )
        verify(userDetailsService, times(1)).loadUserByUsername("admin")
        verify(jwtService, times(1)).generateToken(user)
    }

    @Test
    fun login_incorrecto_lanza_excepcion_mock() {
        val request = AuthResquest(
            username = "admin",
            password = "mal"
        )

        `when`(
            authManager.authenticate(
                UsernamePasswordAuthenticationToken("admin", "mal")
            )
        ).thenThrow(BadCredentialsException("Bad credentials"))

        val authController = AuthController(authManager, userDetailsService, jwtService)

        assertThrows(BadCredentialsException::class.java) {
            authController.login(request)
        }

        verify(authManager, times(1)).authenticate(
            UsernamePasswordAuthenticationToken("admin", "mal")
        )
    }
}