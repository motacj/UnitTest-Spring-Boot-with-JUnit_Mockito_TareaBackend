package es.atomsusej.tareaBackend.integration.repository

import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.dao.MatriculaRepository
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
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
class MatriculaRepositoryIntegrationTest {

    @Autowired
    lateinit var matriculaRepository: MatriculaRepository

    @Autowired
    lateinit var asignaturaRepository: AsignaturaRepository

    @Autowired
    lateinit var personaRepository: PersonaRepository

    @BeforeEach
    fun setUp() {
        // Aquí limpio primero matrículas y después las tablas auxiliares
        matriculaRepository.deleteAll()
        asignaturaRepository.deleteAll()
        personaRepository.deleteAll()
    }

    @Test
    fun save_guarda_correctamente() {
        // Aquí creo antes una persona y una asignatura para construir la matrícula
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

        val id = MatriculaId(asignatura.id_asignatura, persona.id_persona)
        val matricula = Matricula(id, 7.5f)

        // Aquí guardo la matrícula en la base H2
        val guardada = matriculaRepository.save(matricula)

        // Aquí compruebo que la clave y la nota son correctas
        assertEquals(asignatura.id_asignatura, guardada.id.id_asignatura)
        assertEquals(persona.id_persona, guardada.id.id_alumno)
        assertEquals(7.5f, guardada.nota)
    }

    @Test
    fun findById_devuelve_matricula() {
        val persona = personaRepository.save(
            Persona(
                nombre = "Ana",
                apellidos = "López",
                edad = 21
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Lengua",
                horario = "Tarde",
                id_profesor = persona.id_persona
            )
        )

        val id = MatriculaId(asignatura.id_asignatura, persona.id_persona)
        matriculaRepository.save(Matricula(id, 8.0f))

        // Aquí busco la matrícula por su clave compuesta
        val resultado = matriculaRepository.findById(id)

        // Aquí compruebo que existe y que los datos coinciden
        assertTrue(resultado.isPresent)
        assertEquals(asignatura.id_asignatura, resultado.get().id.id_asignatura)
        assertEquals(persona.id_persona, resultado.get().id.id_alumno)
        assertEquals(8.0f, resultado.get().nota)
    }

    @Test
    fun findAll_devuelve_lista() {
        val persona1 = personaRepository.save(
            Persona(
                nombre = "Carlos",
                apellidos = "Ruiz",
                edad = 22
            )
        )

        val persona2 = personaRepository.save(
            Persona(
                nombre = "Laura",
                apellidos = "Gómez",
                edad = 23
            )
        )

        val asignatura1 = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Historia",
                horario = "Mañana",
                id_profesor = persona1.id_persona
            )
        )

        val asignatura2 = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Física",
                horario = "Tarde",
                id_profesor = persona2.id_persona
            )
        )

        matriculaRepository.save(Matricula(MatriculaId(asignatura1.id_asignatura, persona1.id_persona), 6.0f))
        matriculaRepository.save(Matricula(MatriculaId(asignatura2.id_asignatura, persona2.id_persona), 9.0f))

        // Aquí recupero toda la lista
        val matriculas = matriculaRepository.findAll()

        // Aquí compruebo que hay dos matrículas y que contienen las notas esperadas
        assertEquals(2, matriculas.size)
        assertTrue(matriculas.any { it.nota == 6.0f })
        assertTrue(matriculas.any { it.nota == 9.0f })
    }

    @Test
    fun deleteById_elimina_matricula() {
        val persona = personaRepository.save(
            Persona(
                nombre = "Mario",
                apellidos = "Díaz",
                edad = 24
            )
        )

        val asignatura = asignaturaRepository.save(
            Asignatura(
                nombre_asignatura = "Química",
                horario = "Mañana",
                id_profesor = persona.id_persona
            )
        )

        val id = MatriculaId(asignatura.id_asignatura, persona.id_persona)
        matriculaRepository.save(Matricula(id, 5.5f))

        // Aquí borro la matrícula por su clave compuesta
        matriculaRepository.deleteById(id)

        // Aquí compruebo que ya no existe en la base
        val resultado = matriculaRepository.findById(id)
        assertFalse(resultado.isPresent)
    }
}