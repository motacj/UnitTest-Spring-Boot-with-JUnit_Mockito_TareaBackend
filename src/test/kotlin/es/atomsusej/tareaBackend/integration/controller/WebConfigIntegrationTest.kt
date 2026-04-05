package es.atomsusej.tareaBackend.integration.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// Aquí levanto el contexto completo de Spring Boot para probar la configuración real
@SpringBootTest

// Aquí activo MockMvc sin desactivar filtros, porque en este test sí quiero probar CORS
@AutoConfigureMockMvc

// Aquí obligo a usar el perfil de pruebas
@ActiveProfiles("test")
class WebConfigIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun cors_permite_origen_localhost_en_preflight() {
        // Aquí simulo una petición preflight OPTIONS desde localhost
        mockMvc.perform(
            options("/auth/login")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type, Authorization")
        )
            // Aquí compruebo que la preflight responde correctamente
            .andExpect(status().isOk)

            // Aquí compruebo que devuelve el origen permitido
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))

            // Aquí compruebo que permite credenciales
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
    }

    @Test
    fun cors_permite_origen_127001_en_preflight() {
        // Aquí simulo una petición preflight OPTIONS desde 127.0.0.1
        mockMvc.perform(
            options("/auth/login")
                .header("Origin", "http://127.0.0.1:3001")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type, Authorization")
        )
            // Aquí compruebo que responde correctamente
            .andExpect(status().isOk)

            // Aquí compruebo que el origen está permitido
            .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:3001"))

            // Aquí compruebo que permite credenciales
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
    }

    @Test
    fun cors_bloquea_origen_no_permitido() {
        // Aquí simulo una petición desde un origen no permitido
        mockMvc.perform(
            options("/auth/login")
                .header("Origin", "http://evil.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type, Authorization")
        )
            // Aquí compruebo que al no ser un origen permitido devuelve 403
            .andExpect(status().isForbidden)
    }
}