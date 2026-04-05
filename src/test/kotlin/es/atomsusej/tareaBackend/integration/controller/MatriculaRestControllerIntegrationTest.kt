package es.atomsusej.tareaBackend.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.dao.MatriculaRepository
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
import es.atomsusej.tareaBackend.models.Persona
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
class MatriculaRestControllerIntegrationTest {

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

    @BeforeEach
    fun setUp() {
        // Aquí limpio primero las matrículas porque dependen de persona y asignatura
        matriculaRepository.deleteAll()
        asignaturaRepository.deleteAll()
        personaRepository.deleteAll()
    }

    @Test
    fun get_devuelve_datos() {
        // Aquí preparo primero los datos necesarios para que exista una matrícula
        val persona = personaRepository.save(
            Persona(
                nombre = "Juan",
                apellidos = "Pérez",
                edad = 20
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                "Matemáticas",
                "Mañana",
                persona.id_persona
            )
        )

        val matricula = Matricula(
            MatriculaId(asignatura.id_asignatura, persona.id_persona),
            7.5f
        )
        matriculaRepository.save(matricula)

        // Aquí hago el GET al listado de matrículas
        mockMvc.perform(get("/api/v3/matriculas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].nota").value(7.5))
            .andExpect(jsonPath("$[0].id.id_asignatura").value(asignatura.id_asignatura))
            .andExpect(jsonPath("$[0].id.id_alumno").value(persona.id_persona))
    }

    @Test
    fun get_por_id_devuelve_dato() {
        val persona = personaRepository.save(
            Persona(
                nombre = "Ana",
                apellidos = "López",
                edad = 21
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                "Lengua",
                "Tarde",
                persona.id_persona
            )
        )

        val matricula = Matricula(
            MatriculaId(asignatura.id_asignatura, persona.id_persona),
            8.0f
        )
        matriculaRepository.save(matricula)

        // Aquí hago el GET usando la ruta real del controlador: /{id_asignatura}/{id_alumno}
        mockMvc.perform(get("/api/v3/matriculas/${asignatura.id_asignatura}/${persona.id_persona}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nota").value(8.0))
            .andExpect(jsonPath("$.id.id_asignatura").value(asignatura.id_asignatura))
            .andExpect(jsonPath("$.id.id_alumno").value(persona.id_persona))
    }

    @Test
    fun get_por_id_inexistente_devuelve_404() {
        mockMvc.perform(get("/api/v3/matriculas/999/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun post_crea_correctamente() {
        val persona = personaRepository.save(
            Persona(
                nombre = "Carlos",
                apellidos = "Ruiz",
                edad = 22
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                "Historia",
                "Mañana",
                persona.id_persona
            )
        )

        val matriculaNueva = Matricula(
            MatriculaId(asignatura.id_asignatura, persona.id_persona),
            6.5f
        )

        // Aquí hago el POST y compruebo que devuelve 201 Created
        mockMvc.perform(
            post("/api/v3/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matriculaNueva))
        )
            .andExpect(status().isCreated())
            .andExpect(
                header().string(
                    "location",
                    "/api/v3/matriculas/${asignatura.id_asignatura}/${persona.id_persona}"
                )
            )

        // Aquí compruebo que la matrícula existe realmente en la base
        val todas = matriculaRepository.findAll()
        assertTrue(
            todas.any {
                it.id.id_asignatura == asignatura.id_asignatura &&
                        it.id.id_alumno == persona.id_persona &&
                        it.nota == 6.5f
            }
        )
    }

    @Test
    fun put_actualiza_correctamente() {
        val persona = personaRepository.save(
            Persona(
                nombre = "Laura",
                apellidos = "Gómez",
                edad = 23
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                "Física",
                "Tarde",
                persona.id_persona
            )
        )

        val matricula = matriculaRepository.save(
            Matricula(
                MatriculaId(asignatura.id_asignatura, persona.id_persona),
                5.0f
            )
        )

        val matriculaActualizada = Matricula(
            MatriculaId(asignatura.id_asignatura, persona.id_persona),
            9.0f
        )

        // Aquí hago el PUT a la ruta real de tu controlador: /api/v3/matriculas
        mockMvc.perform(
            put("/api/v3/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matriculaActualizada))
        )
            .andExpect(status().isOk)

        // Aquí recargo desde la base y compruebo que la nota cambió
        val recargada = matriculaRepository.findById(matricula.id).orElseThrow()
        assertTrue(recargada.nota == 9.0f)
    }

    @Test
    fun put_matricula_inexistente_devuelve_404() {
        val matriculaInexistente = Matricula(
            MatriculaId(999, 999),
            4.0f
        )

        mockMvc.perform(
            put("/api/v3/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matriculaInexistente))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun delete_elimina_correctamente() {
        val persona = personaRepository.save(
            Persona(
                nombre = "Mario",
                apellidos = "Díaz",
                edad = 24
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                "Química",
                "Mañana",
                persona.id_persona
            )
        )

        val matricula = Matricula(
            MatriculaId(asignatura.id_asignatura, persona.id_persona),
            7.0f
        )
        matriculaRepository.save(matricula)

        // Aquí uso la ruta real del controlador DELETE: /{id_alumno}/{id_asignatura}
        mockMvc.perform(
            delete("/api/v3/matriculas/${persona.id_persona}/${asignatura.id_asignatura}")
        )
            .andExpect(status().isOk)

        // Aquí compruebo que ya no existe en la base
        val sigueExistiendo = matriculaRepository.findAll().any {
            it.id.id_asignatura == asignatura.id_asignatura &&
                    it.id.id_alumno == persona.id_persona
        }

        assertTrue(!sigueExistiendo)
    }

    @Test
    fun delete_matricula_inexistente_devuelve_404() {
        mockMvc.perform(delete("/api/v3/matriculas/999/999"))
            .andExpect(status().isNotFound)
    }
}