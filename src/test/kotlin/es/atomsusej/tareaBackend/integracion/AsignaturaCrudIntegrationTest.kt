package es.atomsusej.tareaBackend.integracion
import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AsignaturaCrudIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var asignaturaRepository: AsignaturaRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `POST debe crear una nueva asignatura`() {

        val nuevaAsignatura = Asignatura(
            id_profesor = 1,
            nombre_asignatura = "Testing",
            horario = "Mañana"
        )

        mockMvc.perform(
            post("/api/v2/asignaturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaAsignatura))
        )
            .andExpect(status().isCreated)

        // Verificamos que realmente existe en BD
        val guardada = asignaturaRepository.findById(9999).orElse(null)

        assertEquals("Testing", guardada.nombre_asignatura)
        assertEquals("Mañana", guardada.horario)
    }

    @Test
    fun `PUT debe actualizar una asignatura existente`() {

        // 1️⃣ Insertamos primero una
        val asignatura = Asignatura(
            id_profesor = 2,
            nombre_asignatura = "Programación",
            horario = "Mañana"
        )

        asignaturaRepository.save(asignatura)

        // 2️⃣ La modificamos
        asignatura.horario = "Tarde"

        mockMvc.perform(
            put("/api/v2/asignaturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignatura))
        )
            .andExpect(status().isOk)

        // 3️⃣ Verificamos cambio real en BD
        val actualizada = asignaturaRepository.findById(8888).orElse(null)

        assertEquals("Tarde", actualizada.horario)
    }
}