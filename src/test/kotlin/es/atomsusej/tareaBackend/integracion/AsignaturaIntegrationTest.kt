package es.atomsusej.tareaBackend.integracion

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 🔥 Seguridad desactivada
class AsignaturaIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `GET asignaturas debe devolver 200`() {

        mockMvc.perform(
            get("/api/v2/asignaturas")
        )
            .andExpect(status().isOk)
    }
}