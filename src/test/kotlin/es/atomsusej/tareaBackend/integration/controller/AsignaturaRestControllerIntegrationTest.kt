package es.atomsusej.tareaBackend.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.utils.ConstantsAsignaturas
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AsignaturaRestControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var asignaturaRepository: AsignaturaRepository

    private val baseUrl = ConstantsAsignaturas.Companion.URL_BASE_ASIGNATURAS

    @BeforeEach
    fun setUp() {
        // Aquí limpio la tabla antes de cada test para que cada prueba sea independiente
        asignaturaRepository.deleteAll()
    }

    @Test
    fun get_devuelve_datos() {
        // Aquí inserto una asignatura de prueba directamente en la base
        val asignatura = Asignatura(
            nombre_asignatura = "Matemáticas",
            horario = "Mañana",
            id_profesor = 1
        )
        asignaturaRepository.save(asignatura)

        // Aquí hago el GET al listado de asignaturas
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].nombre_asignatura").value("Matemáticas"))
            .andExpect(jsonPath("$[0].horario").value("Mañana"))
            .andExpect(jsonPath("$[0].id_profesor").value(1))
    }

    @Test
    fun get_por_id_devuelve_dato() {
        val asignatura = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Lengua",
                horario = "Tarde",
                id_profesor = 2
            )
        )

        mockMvc.perform(get("$baseUrl/${asignatura.id_asignatura}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id_asignatura").value(asignatura.id_asignatura))
            .andExpect(jsonPath("$.nombre_asignatura").value("Lengua"))
            .andExpect(jsonPath("$.horario").value("Tarde"))
            .andExpect(jsonPath("$.id_profesor").value(2))
    }

    @Test
    fun get_por_id_inexistente_devuelve_404() {
        mockMvc.perform(get("$baseUrl/9999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun post_crea_correctamente() {
        val nuevaAsignatura = Asignatura(
            nombre_asignatura = "Historia",
            horario = "Mañana",
            id_profesor = 3
        )

        // Aquí hago el POST y compruebo que devuelve 201 Created
        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaAsignatura))
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists("location"))

        // Aquí compruebo que la asignatura realmente se guardó en la base
        val asignaturas = asignaturaRepository.findAll()
        assertTrue(
            asignaturas.any {
                it.nombre_asignatura == "Historia" &&
                        it.horario == "Mañana" &&
                        it.id_profesor == 3
            }
        )
    }

    @Test
    fun put_actualiza_correctamente() {
        // Aquí creo primero una asignatura en la base
        val asignatura = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Física",
                horario = "Tarde",
                id_profesor = 4
            )
        )

        // Aquí preparo el objeto actualizado
        val asignaturaActualizada = Asignatura(
            nombre_asignatura = "Física Actualizada",
            horario = "Mañana",
            id_profesor = 5
        )
        asignaturaActualizada.id_asignatura = asignatura.id_asignatura

        // Aquí hago el PUT a la ruta real de tu controlador: baseUrl sin /{id}
        mockMvc.perform(
            put(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaActualizada))
        )
            .andExpect(status().isOk)

        // Aquí recargo desde la base y compruebo que sí se actualizó
        val recargada = asignaturaRepository.findById(asignatura.id_asignatura).orElseThrow()
        assertTrue(
            recargada.nombre_asignatura == "Física Actualizada" &&
                    recargada.horario == "Mañana" &&
                    recargada.id_profesor == 5
        )
    }

    @Test
    fun delete_elimina_correctamente() {
        val asignatura = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Química",
                horario = "Mañana",
                id_profesor = 6
            )
        )

        // Aquí hago el DELETE usando la ruta real del controlador
        mockMvc.perform(delete("$baseUrl/${asignatura.id_asignatura}"))
            .andExpect(status().isOk)

        // Aquí compruebo que ya no existe en la base
        assertTrue(asignaturaRepository.findById(asignatura.id_asignatura).isEmpty)
    }

    @Test
    fun delete_asignatura_inexistente_devuelve_404() {
        mockMvc.perform(delete("$baseUrl/9999"))
            .andExpect(status().isNotFound)
    }
}