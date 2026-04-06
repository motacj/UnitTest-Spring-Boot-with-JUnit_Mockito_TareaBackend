package es.atomsusej.tareaBackend.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.security.AuthResquest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// Aquí levanto el contexto completo de Spring Boot para probar varias capas juntas
@SpringBootTest

// Aquí activo MockMvc para lanzar peticiones HTTP simuladas
@AutoConfigureMockMvc(addFilters = false)

// Aquí obligo a usar el perfil de pruebas
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun login_admin_correcto_devuelve_token() {
        // Aquí preparo un login válido usando el usuario real definido en UsersConfig (apartado 3.6)
        val loginRequest = AuthResquest(
            username = "admin",
            password = "1234"
        )

        // Aquí hago la petición POST al endpoint de login
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            // Aquí compruebo que el login responde correctamente
            .andExpect(status().isOk)

            // Aquí compruebo que el JSON devuelto contiene un token
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun login_user_correcto_devuelve_token() {
        // Aquí preparo otro login válido con el usuario normal definido en UsersConfig
        val loginRequest = AuthResquest(
            username = "user",
            password = "1234"
        )

        // Aquí hago la petición POST al endpoint de login
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            // Aquí compruebo que responde correctamente
            .andExpect(status().isOk)

            // Aquí compruebo que también devuelve token
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun login_incorrecto_devuelve_401() {
        val loginRequest = AuthResquest(
            username = "admin",
            password = "mal"
        )

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(content().string("Credenciales incorrectas"))
    }
}