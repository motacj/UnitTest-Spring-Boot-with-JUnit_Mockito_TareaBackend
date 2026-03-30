package es.atomsusej.tareaBackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
open class WebConfig {

    @Bean
    open fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()

        // Permite el envío de cookies o headers de autorización (aunque no los uses, es buena práctica)
        config.allowCredentials = true

        // Usamos comodines para cubrir localhost:3001 y 127.0.0.1:3001
        // Esto cubre cualquier puerto en localhost, incluyendo 3000, 3001, etc.
        config.allowedOriginPatterns = listOf("http://localhost:[*]", "http://127.0.0.1:[*]")

        // Permitimos todos los métodos
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")

        // Permitimos todos los encabezados
        config.allowedHeaders = listOf("*")

        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }
}