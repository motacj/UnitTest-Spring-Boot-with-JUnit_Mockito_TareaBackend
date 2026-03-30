package es.atomsusej.tareaBackend

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc // ⚠️ NO desactivar filtros
class AsignaturaSecurityTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `GET asignaturas sin token debe devolver 403`() {

        mockMvc.perform(
            get("/api/v2/asignaturas")
        )
            .andExpect(status().isForbidden)
    }
}