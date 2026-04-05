package es.atomsusej.tareaBackend.web


import es.atomsusej.tareaBackend.security.AuthResponse
import es.atomsusej.tareaBackend.security.AuthResquest
import es.atomsusej.tareaBackend.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun login(@RequestBody req: AuthResquest): ResponseEntity<Any> {
        return try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(req.username, req.password)
            )

            val user = userDetailsService.loadUserByUsername(req.username)
            val token = jwtService.generateToken(user)

            ResponseEntity.ok(AuthResponse(token))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas")
        }
    }
}