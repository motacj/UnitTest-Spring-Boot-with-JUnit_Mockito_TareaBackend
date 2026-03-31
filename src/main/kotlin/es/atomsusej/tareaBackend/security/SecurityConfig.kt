package es.atomsusej.tareaBackend.config

import es.atomsusej.tareaBackend.security.JwtAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {

    // AuthenticationManager: lo usa Spring para validar username/password en /login
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .cors{}
            .csrf { it.disable() } // para APIs REST suele deshabilitarse
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // sin sesiones
            .authorizeHttpRequests { auth ->
            auth.requestMatchers("/auth/**").permitAll()
            //auth.requestMatchers("/api/v1/**", "/api/v2/**", "/api/v3/**").authenticated()
                auth.requestMatchers("/api/v1/**", "/api/v2/**", "/api/v3/**").permitAll()
            auth.anyRequest().permitAll()
        }
            // OJO: este filtro va ANTES del filtro de usuario/contraseña de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}