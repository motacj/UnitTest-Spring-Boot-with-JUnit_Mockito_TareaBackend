package es.atomsusej.tareaBackend.integration.fullflow

import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.dao.MatriculaRepository
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
import es.atomsusej.tareaBackend.models.Persona
import es.atomsusej.tareaBackend.utils.ConstantsMatriculas
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

// Aquí levanto el contexto completo de Spring Boot para probar el flujo entero
@SpringBootTest

// Aquí activo MockMvc para lanzar peticiones HTTP simuladas
// Desactivo filtros para no bloquearme con la seguridad en este fullflow
@AutoConfigureMockMvc(addFilters = false)

// Aquí obligo a usar el perfil de pruebas
@ActiveProfiles("test")
class MatriculaFullFlowIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var matriculaRepository: MatriculaRepository

    @Autowired
    lateinit var asignaturaRepository: AsignaturaRepository

    @Autowired
    lateinit var personaRepository: PersonaRepository

    private val baseUrl = ConstantsMatriculas.Companion.URL_BASE_MATRICULAS

    @BeforeEach
    fun setUp() {
        // Aquí limpio primero las matrículas porque dependen de las otras tablas
        matriculaRepository.deleteAll()
        asignaturaRepository.deleteAll()
        personaRepository.deleteAll()
    }

    @Test
    fun fullflow_matricula_crear_listar_cargar_actualizar_borrar() {
        // Creo personas y asignaturas necesaria para realizar el test
        val persona = personaRepository.save(
            Persona(
                nombre = "Juan",
                apellidos = "Pérez",
                edad = 20
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Matemáticas",
                horario = "Mañana",
                id_profesor = persona.id_persona
            )
        )

        // Aquí preparo la matrícula inicial
        val matriculaNueva = Matricula(
            id = MatriculaId(
                id_asignatura = asignatura.id_asignatura,
                id_alumno = persona.id_persona
            ),
            nota = 7.5f
        )

        // CREO UNA MATRICULA CON UN POST
        mockMvc.perform(
            MockMvcRequestBuilders.post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matriculaNueva))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers.header().string(
                    "location",
                    "$baseUrl/${asignatura.id_asignatura}/${persona.id_persona}"
                )
            )

        // Aquí compruebo que realmente se guardó en la base
        val matriculasDespuesDelPost = matriculaRepository.findAll()
        Assertions.assertTrue(matriculasDespuesDelPost.size == 1)

        val matriculaGuardada = matriculasDespuesDelPost.first()
        Assertions.assertTrue(matriculaGuardada.id.id_asignatura == asignatura.id_asignatura)
        Assertions.assertTrue(matriculaGuardada.id.id_alumno == persona.id_persona)
        Assertions.assertTrue(matriculaGuardada.nota == 7.5f)

        // LISTO LAS MATRICULAS CON UN GET
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id.id_asignatura").value(asignatura.id_asignatura))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id.id_alumno").value(persona.id_persona))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].nota").value(7.5))

        // GRABO UNA MATRICULA CON SU CABLE COMPUESTA
        mockMvc.perform(MockMvcRequestBuilders.get("$baseUrl/${asignatura.id_asignatura}/${persona.id_persona}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id.id_asignatura").value(asignatura.id_asignatura))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id.id_alumno").value(persona.id_persona))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nota").value(7.5))

        // ACTAULIZAR LA MATRICULA PUT
        val matriculaActualizada = Matricula(
            id = MatriculaId(
                id_asignatura = asignatura.id_asignatura,
                id_alumno = persona.id_persona
            ),
            nota = 9.0f
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matriculaActualizada))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Aquí compruebo en base que la nota cambió
        val recargada = matriculaRepository.findAll().first()
        Assertions.assertTrue(recargada.nota == 9.0f)

        // DELETE DE MATRICULA
        // En tu controlador DELETE la ruta está como /{id_alumno}/{id_asignatura}
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$baseUrl/${persona.id_persona}/${asignatura.id_asignatura}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Aquí compruebo que ya no existe en la base
        Assertions.assertTrue(matriculaRepository.findAll().isEmpty())
    }
}