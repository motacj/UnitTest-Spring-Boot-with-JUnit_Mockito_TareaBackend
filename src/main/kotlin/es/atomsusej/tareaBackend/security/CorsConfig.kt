package es.atomsusej.tareaBackend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        // Orígenes permitidos (Live Server / localhost)
        config.allowedOrigins = listOf(
            "http://localhost:5500",
            "http://127.0.0.1:5500",
            "http://localhost:5173",   // por si usas Vite
            "http://127.0.0.1:5173",
            "http://localhost:3000",   // asi admite tu frontend
            "http://127.0.0.1:3000"    // asi admite tu frontend
        )

        // Métodos permitidos
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")

        // Headers permitidos (IMPORTANTE para Authorization)
        config.allowedHeaders = listOf("Authorization", "Content-Type")

        // JWT por header -> normalmente no necesitas cookies
        config.allowCredentials = false

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}