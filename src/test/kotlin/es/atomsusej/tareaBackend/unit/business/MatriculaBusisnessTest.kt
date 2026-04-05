package es.atomsusej.tareaBackend.unit.business

import es.atomsusej.tareaBackend.business.MatriculaBusisness
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.dao.MatriculaRepository
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
import es.atomsusej.tareaBackend.models.Persona
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.util.Optional

// Aquí indico que voy a usar Mockito junto con JUnit 5 para simular dependencias
@ExtendWith(MockitoExtension::class)
class MatriculaBusisnessTest {

    // Aquí simulo el repositorio principal de matrículas
    @Mock
    lateinit var matriculaRepository: MatriculaRepository

    // Aquí simulo el repositorio de asignaturas porque el método save() lo usa
    @Mock
    lateinit var asignaturaRepository: AsignaturaRepository

    // Aquí simulo el repositorio de alumnos/personas porque el método save() también lo usa
    @Mock
    lateinit var alumnoRepository: PersonaRepository

    // Aquí declaro manualmente la clase de negocio
    // Error 01
    // No uso @InjectMocks porque en MatriculaBusisness los repositorios están declarados como val,
    // y Mockito no puede inyectarlos bien al ser campos finales.
    lateinit var matriculaBusiness: MatriculaBusisness

    @BeforeEach
    fun setUp() {
        // Aquí creo yo mismo la instancia del business
        matriculaBusiness = MatriculaBusisness()

        // Aquí inyecto los mocks usando reflection porque así sí puedo meterlos
        // aunque en la clase real los atributos sean val
        ReflectionTestUtils.setField(matriculaBusiness, "MatriculaRepository", matriculaRepository)
        ReflectionTestUtils.setField(matriculaBusiness, "asignaturaRepository", asignaturaRepository)
        ReflectionTestUtils.setField(matriculaBusiness, "alumnoRepository", alumnoRepository)
    }

    @Test
    fun getMatriculaRepository() {
        // Aquí compruebo que la clase se ha creado correctamente
        assertNotNull(matriculaBusiness)

        // Aquí compruebo que el repositorio quedó inyectado correctamente
        assertNotNull(ReflectionTestUtils.getField(matriculaBusiness, "MatriculaRepository"))
    }

    @Test
    fun getAsignaturaRepository() {
        // Aquí compruebo que el repositorio de asignaturas quedó inyectado
        assertNotNull(ReflectionTestUtils.getField(matriculaBusiness, "asignaturaRepository"))
    }

    @Test
    fun getAlumnoRepository() {
        // Aquí compruebo que el repositorio de alumnos quedó inyectado
        assertNotNull(ReflectionTestUtils.getField(matriculaBusiness, "alumnoRepository"))
    }

    @Test
    fun list() {
        // Aquí preparo dos matrículas de ejemplo para simular datos existentes
        val matricula1 = Matricula(MatriculaId(1, 1), 7.5f)
        val matricula2 = Matricula(MatriculaId(2, 1), 8.0f)
        val listaEsperada = listOf(matricula1, matricula2)

        // Aquí le digo al mock que cuando se llame a findAll devuelva mi lista
        `when`(matriculaRepository.findAll()).thenReturn(listaEsperada)

        // Ejecuto el método que quiero evaluar
        val resultado = matriculaBusiness.list()

        // Aquí compruebo que el resultado es el esperado
        assertEquals(2, resultado.size)
        assertEquals(listaEsperada, resultado)

        // Aquí verifico que el repositorio se llamó una sola vez
        verify(matriculaRepository, times(1)).findAll()
    }

    @Test
    fun load() {
        // Aquí preparo una matrícula concreta
        val matricula = Matricula(MatriculaId(1, 1), 9.0f)

        // Error 02
        // Aquí uso any(MatriculaId::class.java) porque el método crea un id nuevo dentro de la función load()
        // y Mockito no lo reconoce como el mismo objeto que el del test
        `when`(matriculaRepository.findById(any(MatriculaId::class.java)))
            .thenReturn(Optional.of(matricula))

        // Aquí ejecuto la carga usando los dos ids
        val resultado = matriculaBusiness.load(1, 1)

        // Aquí compruebo que devuelve la matrícula correcta
        assertEquals(matricula, resultado)

        // Aquí verifico que findById se llamó una sola vez
        verify(matriculaRepository, times(1)).findById(any(MatriculaId::class.java))
    }

    @Test
    fun save() {
        // Aquí preparo una matrícula válida
        val matricula = Matricula(MatriculaId(1, 1), 6.5f)

        // Error 03
        // El metodo no solo una MatriculaRepository, también consulta asignaturaRepository y alumnoRepository,
        // y si no los simulo acaba lanzando excepción.
        val asignatura = mock(Asignatura::class.java)
        val alumno = Persona(1, "Juan", "Pérez", 20)

        // Aquí simulo que la asignatura sí existe
        `when`(asignaturaRepository.findById(1)).thenReturn(Optional.of(asignatura))

        // Aquí simulo que el alumno sí existe
        `when`(alumnoRepository.findById(1)).thenReturn(Optional.of(alumno))

        // Aquí simulo que se guarda correctamente la matrícula
        `when`(matriculaRepository.save(matricula)).thenReturn(matricula)

        // Aquí ejecuto el método save()
        val resultado = matriculaBusiness.save(matricula)

        // Aquí compruebo que devuelve la matrícula esperada
        assertEquals(matricula, resultado)

        // Aquí verifico que primero se comprobaron asignatura y alumno, y luego se guardó
        verify(asignaturaRepository, times(1)).findById(1)
        verify(alumnoRepository, times(1)).findById(1)
        verify(matriculaRepository, times(1)).save(matricula)
    }

    @Test
    fun remove() {
        // Aquí preparo una matrícula existente
        val matricula = Matricula(MatriculaId(1, 1), 5.0f)

        // Error 04 igual que el Error 02
        `when`(matriculaRepository.findById(any(MatriculaId::class.java)))
            .thenReturn(Optional.of(matricula))

        // Aquí indico que el borrado no debe fallar
        doNothing().`when`(matriculaRepository).deleteById(any(MatriculaId::class.java))

        // Aquí ejecuto el borrado
        matriculaBusiness.remove(1, 1)

        // Aquí verifico que primero buscó la matrícula
        verify(matriculaRepository, times(1)).findById(any(MatriculaId::class.java))

        // Aquí verifico que después la borró
        verify(matriculaRepository, times(1)).deleteById(any(MatriculaId::class.java))
    }

    @Test
    fun load_matriculaNoEncontrada_lanzaExcepcion() {
        // Aquí simulo que la matrícula no existe
        `when`(matriculaRepository.findById(any(MatriculaId::class.java)))
            .thenReturn(Optional.empty())

        // Error 05
        // Cuando la matrícula no existe, la excepción correcta es NotFoundException.
        // BusinessException solo se lanza si falla el repositorio dentro del try/catch.
        assertThrows(NotFoundException::class.java) {
            matriculaBusiness.load(99, 99)
        }

        // Aquí verifico que sí se intentó buscar
        verify(matriculaRepository, times(1)).findById(any(MatriculaId::class.java))
    }

    @Test
    fun remove_matriculaNoExiste_lanzaExcepcion() {
        // Aquí simulo que la matrícula no existe
        `when`(matriculaRepository.findById(any(MatriculaId::class.java)))
            .thenReturn(Optional.empty())

        // Aquí compruebo que remove() lanza NotFoundException cuando no encuentra la matrícula
        assertThrows(NotFoundException::class.java) {
            matriculaBusiness.remove(99, 99)
        }

        // Aquí verifico que sí se intentó buscar
        verify(matriculaRepository, times(1)).findById(any(MatriculaId::class.java))

        // Aquí verifico que nunca se intentó borrar porque no existía
        verify(matriculaRepository, never()).deleteById(any(MatriculaId::class.java))
    }
}
