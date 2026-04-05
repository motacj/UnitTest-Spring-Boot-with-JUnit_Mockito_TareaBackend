package es.atomsusej.tareaBackend.integration.controller

import jakarta.servlet.ServletException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun secure_con_usuario_autenticado_devuelve_mensaje() {
        mockMvc.perform(get("/api/test"))
            .andExpect(status().isOk)
            .andExpect(content().string("Entraste con JWT. Usuario: admin"))
    }

    @Test
    fun secure_sin_autenticar_lanza_excepcion() {
        val exception = assertThrows(ServletException::class.java) {
            mockMvc.perform(get("/api/test"))
        }

        assertTrue(exception.cause is NullPointerException)
    }
}