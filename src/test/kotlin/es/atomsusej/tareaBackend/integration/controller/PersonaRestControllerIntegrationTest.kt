package es.atomsusej.tareaBackend.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import es.atomsusej.tareaBackend.dao.PersonaRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// Aquí levanto el contexto completo de Spring Boot para probar varias capas juntas
@SpringBootTest

// Aquí activo MockMvc para lanzar peticiones HTTP simuladas sin arrancar servidor real
// Pongo addFilters = false para que la seguridad no me bloquee mientras pruebo el flujo
@AutoConfigureMockMvc(addFilters = false)

// Aquí obligo a que el test use el perfil de pruebas y no mi base de datos real
@ActiveProfiles("test")
class PersonaRestControllerIntegrationTest {

    // Aquí inyecto MockMvc para poder hacer GET, POST, PUT y DELETE contra el controlador
    @Autowired
    lateinit var mockMvc: MockMvc

    // Aquí inyecto ObjectMapper para convertir objetos Kotlin a JSON
    @Autowired
    lateinit var objectMapper: ObjectMapper

    // Aquí inyecto el repositorio para insertar datos de prueba y comprobar la persistencia
    @Autowired
    lateinit var personaRepository: PersonaRepository

    @BeforeEach
    fun setUp() {
        // Aquí limpio la tabla antes de cada test para que cada prueba sea independiente
        personaRepository.deleteAll()
    }

    @Test
    fun get_devuelve_datos() {
        // Aquí inserto una persona de prueba directamente en la base H2
        val persona = Persona(
            nombre = "Juan",
            apellidos = "Pérez",
            edad = 30
        )
        personaRepository.save(persona)

        // Aquí lanzo el GET al endpoint de personas
        mockMvc.perform(get("/api/v1/personas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].nombre").value("Juan"))
            .andExpect(jsonPath("$[0].apellidos").value("Pérez"))
            .andExpect(jsonPath("$[0].edad").value(30))
    }

    @Test
    fun post_crea_correctamente() {
        // Aquí preparo una persona nueva para mandarla por POST
        val nuevaPersona = Persona(
            nombre = "Ana",
            apellidos = "López",
            edad = 25
        )

        // Aquí llamo al endpoint POST enviando JSON
        mockMvc.perform(
            post("/api/v1/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaPersona))
        )
            // ********************Error en la primera prueba del test*******************
            // Esta respondiendo 201 no 200OK y por tanto cambaimos esta linea
            //.andExpect(status().isOk)
            .andExpect(status().isCreated)

        // Aquí compruebo en la base que la persona realmente se ha guardado
        val personas = personaRepository.findAll()
        assertTrue(
            personas.any {
                it.nombre == "Ana" &&
                        it.apellidos == "López" &&
                        it.edad == 25
            }
        )
    }

    @Test
    fun put_actualiza_correctamente() {
        // Aquí creo una persona inicial en la base
        val persona = personaRepository.save(
            Persona(
                nombre = "Carlos",
                apellidos = "Ruiz",
                edad = 40
            )
        )

        // Aquí preparo el objeto actualizado
        val personaActualizada = Persona(
            id_persona = persona.id_persona,
            nombre = "Carlos",
            apellidos = "Ruiz Actualizado",
            edad = 41
        )

        // Aquí hago la petición PUT para actualizar
        mockMvc.perform(
            //**********************Error del primer pruba del test************
            //No tiene id_persona le controller original asi que sustituimos por
            // put("/api/v1/personas/${persona.id_persona}")
            put("/api/v1/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personaActualizada))
        )
            .andExpect(status().isOk)

        // Aquí recargo desde la base y compruebo que sí se actualizó
        val recargada = personaRepository.findById(persona.id_persona).orElseThrow()
        assertTrue(recargada.apellidos == "Ruiz Actualizado" && recargada.edad == 41)
    }

    @Test
    fun delete_elimina_correctamente() {
        // Aquí creo una persona para luego borrarla
        val persona = personaRepository.save(
            Persona(
                nombre = "Laura",
                apellidos = "Gómez",
                edad = 28
            )
        )

        // Aquí hago la petición DELETE
        mockMvc.perform(delete("/api/v1/personas/${persona.id_persona}"))
            .andExpect(status().isOk)

        // Aquí compruebo que ya no existe en la base
        assertTrue(personaRepository.findById(persona.id_persona).isEmpty)
    }

    @Test
    fun get_por_id_inexistente_devuelve_404() {
        // Aquí compruebo que pedir un id que no existe devuelve 404
        mockMvc.perform(get("/api/v1/personas/9999"))
            .andExpect(status().isNotFound)
    }
}
