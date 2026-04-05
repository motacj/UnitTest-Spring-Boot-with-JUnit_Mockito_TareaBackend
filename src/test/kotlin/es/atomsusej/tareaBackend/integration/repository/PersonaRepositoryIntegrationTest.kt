package es.atomsusej.tareaBackend.integration.repository

import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.models.Persona
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

// Aquí levanto solo la parte JPA para probar el repositorio con la base de datos de test
@DataJpaTest

// Aquí obligo a usar el perfil de pruebas
@ActiveProfiles("test")
class PersonaRepositoryIntegrationTest {

    @Autowired
    lateinit var personaRepository: PersonaRepository

    @BeforeEach
    fun setUp() {
        // Aquí limpio la tabla antes de cada test para que las pruebas sean independientes
        personaRepository.deleteAll()
    }

    @Test
    fun save_guarda_correctamente() {
        // Aquí preparo una persona nueva
        val persona = Persona(
            nombre = "Juan",
            apellidos = "Pérez",
            edad = 30
        )

        // Aquí la guardo en la base H2
        val guardada = personaRepository.save(persona)

        // Aquí compruebo que se le ha generado un id y que los datos son correctos
        assertTrue(guardada.id_persona > 0)
        assertEquals("Juan", guardada.nombre)
        assertEquals("Pérez", guardada.apellidos)
        assertEquals(30, guardada.edad)
    }

    @Test
    fun findById_devuelve_persona() {
        // Aquí guardo primero una persona para luego buscarla
        val personaGuardada = personaRepository.save(
            Persona(
                nombre = "Ana",
                apellidos = "López",
                edad = 25
            )
        )

        // Aquí busco la persona por su id
        val resultado = personaRepository.findById(personaGuardada.id_persona)

        // Aquí compruebo que sí existe y que los datos coinciden
        assertTrue(resultado.isPresent)
        assertEquals("Ana", resultado.get().nombre)
        assertEquals("López", resultado.get().apellidos)
        assertEquals(25, resultado.get().edad)
    }

    @Test
    fun findAll_devuelve_lista() {
        // Aquí inserto varias personas
        personaRepository.save(
            Persona(
                nombre = "Carlos",
                apellidos = "Ruiz",
                edad = 40
            )
        )

        personaRepository.save(
            Persona(
                nombre = "Laura",
                apellidos = "Gómez",
                edad = 28
            )
        )

        // Aquí recupero toda la lista
        val personas = personaRepository.findAll()

        // Aquí compruebo que hay dos elementos y que contienen los datos esperados
        assertEquals(2, personas.size)
        assertTrue(personas.any { it.nombre == "Carlos" })
        assertTrue(personas.any { it.nombre == "Laura" })
    }

    @Test
    fun deleteById_elimina_persona() {
        // Aquí guardo una persona
        val personaGuardada = personaRepository.save(
            Persona(
                nombre = "Mario",
                apellidos = "Díaz",
                edad = 35
            )
        )

        // Aquí la borro por id
        personaRepository.deleteById(personaGuardada.id_persona)

        // Aquí compruebo que ya no existe en la base
        val resultado = personaRepository.findById(personaGuardada.id_persona)
        assertFalse(resultado.isPresent)
    }
}