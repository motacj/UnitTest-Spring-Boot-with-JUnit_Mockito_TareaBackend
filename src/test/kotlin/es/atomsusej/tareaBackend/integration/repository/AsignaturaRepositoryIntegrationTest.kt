package es.atomsusej.tareaBackend.integration.repository

import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.models.Asignatura
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
class AsignaturaRepositoryIntegrationTest {

    @Autowired
    lateinit var asignaturaRepository: AsignaturaRepository

    @BeforeEach
    fun setUp() {
        // Aquí limpio la tabla antes de cada test para que las pruebas sean independientes
        asignaturaRepository.deleteAll()
    }

    @Test
    fun save_guarda_correctamente() {
        // Aquí preparo una asignatura nueva
        val asignatura = Asignatura(
            nombre_asignatura = "Matemáticas",
            horario = "Mañana",
            id_profesor = 1
        )

        // Aquí la guardo en la base H2
        val guardada = asignaturaRepository.save(asignatura)

        // Aquí compruebo que se le ha generado un id y que los datos son correctos
        assertTrue(guardada.id_asignatura > 0)
        assertEquals("Matemáticas", guardada.nombre_asignatura)
        assertEquals("Mañana", guardada.horario)
        assertEquals(1, guardada.id_profesor)
    }

    @Test
    fun findById_devuelve_asignatura() {
        // Aquí guardo primero una asignatura para luego buscarla
        val asignaturaGuardada = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Lengua",
                horario = "Tarde",
                id_profesor = 2
            )
        )

        // Aquí busco la asignatura por su id
        val resultado = asignaturaRepository.findById(asignaturaGuardada.id_asignatura)

        // Aquí compruebo que sí existe y que los datos coinciden
        assertTrue(resultado.isPresent)
        assertEquals("Lengua", resultado.get().nombre_asignatura)
        assertEquals("Tarde", resultado.get().horario)
        assertEquals(2, resultado.get().id_profesor)
    }

    @Test
    fun findAll_devuelve_lista() {
        // Aquí inserto varias asignaturas
        asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Historia",
                horario = "Mañana",
                id_profesor = 3
            )
        )

        asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Física",
                horario = "Tarde",
                id_profesor = 4
            )
        )

        // Aquí recupero toda la lista
        val asignaturas = asignaturaRepository.findAll()

        // Aquí compruebo que hay dos elementos y que contienen los datos esperados
        assertEquals(2, asignaturas.size)
        assertTrue(asignaturas.any { it.nombre_asignatura == "Historia" })
        assertTrue(asignaturas.any { it.nombre_asignatura == "Física" })
    }

    @Test
    fun deleteById_elimina_asignatura() {
        // Aquí guardo una asignatura
        val asignaturaGuardada = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Química",
                horario = "Mañana",
                id_profesor = 5
            )
        )

        // Aquí la borro por ID
        asignaturaRepository.deleteById(asignaturaGuardada.id_asignatura)

        // Aquí compruebo que ya no existe en la base
        val resultado = asignaturaRepository.findById(asignaturaGuardada.id_asignatura)
        assertFalse(resultado.isPresent)
    }
}