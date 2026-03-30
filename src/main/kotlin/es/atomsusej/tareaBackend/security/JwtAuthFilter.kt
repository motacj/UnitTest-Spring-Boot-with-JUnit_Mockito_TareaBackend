package es.atomsusej.tareaBackend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        // Si no hay token, seguimos (Spring Security decidirá si es público o no)
        if (header.isNullOrBlank() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix("Bearer ").trim()

        val username = try {
            jwtService.extractUsername(token)
        } catch (_: Exception) {
            // Token mal formado -> seguimos sin autenticar (acabará en 401/403 según reglas)
            filterChain.doFilter(request, response)
            return
        }

        // Si ya hay auth en el contexto, no pisamos
        if (SecurityContextHolder.getContext().authentication != null) {
            filterChain.doFilter(request, response)
            return
        }

        val user = try {
            userDetailsService.loadUserByUsername(username)
        } catch (_: Exception) {
            filterChain.doFilter(request, response)
            return
        }

        // Validación del token
        val valid = try {
            jwtService.isTokenValid(token, user)
        } catch (_: Exception) {
            false
        }

        if (valid) {
            // ✅ Roles desde el JWT (claim "roles": ["ROLE_ADMIN", ...])
            val rolesFromJwt: List<String> = try {
                jwtService.extractRoles(token)
            } catch (_: Exception) {
                emptyList()
            }

            val authorities = rolesFromJwt
                .filter { it.isNotBlank() }
                .map { SimpleGrantedAuthority(it.trim()) }

            val authToken = UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
            )

            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authToken
        }

        filterChain.doFilter(request, response)
    }
}