package es.atomsusej.tareaBackend.web


import es.atomsusej.tareaBackend.security.AuthResponse
import es.atomsusej.tareaBackend.security.AuthResquest
import es.atomsusej.tareaBackend.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody req: AuthResquest): AuthResponse {

        // 1) Spring comprueba usuario/contraseña (contra UserDetailsService)
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(req.username, req.password)
        )

        // 2) Si llega aquí: login correcto. Cargo el usuario.
        val user = userDetailsService.loadUserByUsername(req.username)

        // 3) Creo token JWT
        val token = jwtService.generateToken(user)

        // 4) Devuelvo token al cliente
        return AuthResponse(token)
    }
}