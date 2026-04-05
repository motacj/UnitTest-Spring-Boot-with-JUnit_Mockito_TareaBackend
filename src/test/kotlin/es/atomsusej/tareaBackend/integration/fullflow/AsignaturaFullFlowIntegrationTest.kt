package es.atomsusej.tareaBackend.integration.fullflow

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

// Aquí levanto el contexto completo de Spring Boot para probar el flujo entero
@SpringBootTest

// Aquí activo MockMvc para lanzar peticiones HTTP simuladas
// Desactivo filtros para no bloquearme con seguridad mientras pruebo el flujo
@AutoConfigureMockMvc(addFilters = false)

// Aquí obligo a usar el perfil de pruebas
@ActiveProfiles("test")
class AsignaturaFullFlowIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var asignaturaRepository: AsignaturaRepository

    private val baseUrl = ConstantsAsignaturas.URL_BASE_ASIGNATURAS

    @BeforeEach
    fun setUp() {
        // Aquí limpio la tabla antes de cada test para que el flujo empiece desde cero
        asignaturaRepository.deleteAll()
    }

    @Test
    fun fullflow_asignatura_crear_listar_cargar_actualizar_borrar() {
        // POST PARA VRECAR UNA ASIGNATURA
        val nuevaAsignatura = Asignatura(
            nombre_asignatura = "Matemáticas",
            horario = "Mañana",
            id_profesor = 1
        )

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaAsignatura))
        )
            // En tu controlador el POST devuelve 201 Created
            .andExpect(status().isCreated())
            .andExpect(header().exists("location"))

        // Aquí compruebo que realmente se guardó en la base
        val asignaturasDespuesDelPost = asignaturaRepository.findAll()
        assertTrue(asignaturasDespuesDelPost.size == 1)

        val asignaturaGuardada = asignaturasDespuesDelPost.first()
        val idGenerado = asignaturaGuardada.id_asignatura

        assertTrue(asignaturaGuardada.nombre_asignatura == "Matemáticas")
        assertTrue(asignaturaGuardada.horario == "Mañana")
        assertTrue(asignaturaGuardada.id_profesor == 1)

        // LISTAR ASIGNATURAS GET
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id_asignatura").value(idGenerado))
            .andExpect(jsonPath("$[0].nombre_asignatura").value("Matemáticas"))
            .andExpect(jsonPath("$[0].horario").value("Mañana"))
            .andExpect(jsonPath("$[0].id_profesor").value(1))

        // CARGAR ASIGNATURAS POR EL ID
        mockMvc.perform(get("$baseUrl/$idGenerado"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id_asignatura").value(idGenerado))
            .andExpect(jsonPath("$.nombre_asignatura").value("Matemáticas"))
            .andExpect(jsonPath("$.horario").value("Mañana"))
            .andExpect(jsonPath("$.id_profesor").value(1))

        // PUT DE ACTUALIZACION
        val asignaturaActualizada = Asignatura(
            nombre_asignatura = "Matemáticas Avanzadas",
            horario = "Tarde",
            id_profesor = 2
        )
        asignaturaActualizada.id_asignatura = idGenerado

        // En tu controlador real el PUT va a /api/v2/asignaturas sin /{id}
        mockMvc.perform(
            put(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaActualizada))
        )
            .andExpect(status().isOk)

        // Aquí compruebo en base que la actualización sí se hizo
        val recargada = asignaturaRepository.findById(idGenerado).orElseThrow()
        assertTrue(recargada.nombre_asignatura == "Matemáticas Avanzadas")
        assertTrue(recargada.horario == "Tarde")
        assertTrue(recargada.id_profesor == 2)

        // BORRADO DE ASIGNATURA
        mockMvc.perform(delete("$baseUrl/$idGenerado"))
            .andExpect(status().isOk)

        // Aquí compruebo que ya no existe
        assertTrue(asignaturaRepository.findById(idGenerado).isEmpty)
    }
}