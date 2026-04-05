package es.atomsusej.tareaBackend.integration.fullflow

import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.models.Persona
import es.atomsusej.tareaBackend.utils.ConstantsPersonas
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
// Desactivo filtros para no bloquearme con la seguridad en este fullflow
@AutoConfigureMockMvc(addFilters = false)

// Aquí obligo a usar el perfil de pruebas
@ActiveProfiles("test")
class PersonaFullFlowIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var personaRepository: PersonaRepository

    private val baseUrl = ConstantsPersonas.URL_BASE_PERSONAS

    @BeforeEach
    fun setUp() {
        // Aquí limpio la tabla antes de cada test para que el flujo empiece desde cero
        personaRepository.deleteAll()
    }

    @Test
    fun fullflow_persona_crear_listar_cargar_actualizar_borrar() {
        // POST PARA PERSONA
        val nuevaPersona = Persona(
            nombre = "Juan",
            apellidos = "Pérez",
            edad = 30
        )

        mockMvc.perform(
            post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaPersona))
        )
            // El controlador el POST devuelve 201 Created
            .andExpect(status().isCreated())
            .andExpect(header().exists("location"))

        // Aquí compruebo que realmente se guardó en la base
        val personasDespuesDelPost = personaRepository.findAll()
        assertTrue(personasDespuesDelPost.size == 1)

        val personaGuardada = personasDespuesDelPost.first()
        val idGenerado = personaGuardada.id_persona

        assertTrue(personaGuardada.nombre == "Juan")
        assertTrue(personaGuardada.apellidos == "Pérez")
        assertTrue(personaGuardada.edad == 30)

        // GET LISTA DE PERSONAS
        mockMvc.perform(get(baseUrl))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id_persona").value(idGenerado))
            .andExpect(jsonPath("$[0].nombre").value("Juan"))
            .andExpect(jsonPath("$[0].apellidos").value("Pérez"))
            .andExpect(jsonPath("$[0].edad").value(30))

        // CARGAR PERSONA POR SU ID
        mockMvc.perform(get("$baseUrl/$idGenerado"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id_persona").value(idGenerado))
            .andExpect(jsonPath("$.nombre").value("Juan"))
            .andExpect(jsonPath("$.apellidos").value("Pérez"))
            .andExpect(jsonPath("$.edad").value(30))

        // PUT PARA UNA PERSONA
        val personaActualizada = Persona(
            id_persona = idGenerado,
            nombre = "Juan Carlos",
            apellidos = "Pérez Gómez",
            edad = 31
        )

        // El controlador real el PUT va a /api/v1/personas sin /{id}
        mockMvc.perform(
            put(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personaActualizada))
        )
            .andExpect(status().isOk)

        // Aquí compruebo en base que la actualización sí se hizo
        val recargada = personaRepository.findById(idGenerado).orElseThrow()
        assertTrue(recargada.nombre == "Juan Carlos")
        assertTrue(recargada.apellidos == "Pérez Gómez")
        assertTrue(recargada.edad == 31)

        // DELETE PARA PERSONA
        mockMvc.perform(delete("$baseUrl/$idGenerado"))
            .andExpect(status().isOk)

        // Aquí compruebo que ya no existe
        assertTrue(personaRepository.findById(idGenerado).isEmpty)
    }
}
