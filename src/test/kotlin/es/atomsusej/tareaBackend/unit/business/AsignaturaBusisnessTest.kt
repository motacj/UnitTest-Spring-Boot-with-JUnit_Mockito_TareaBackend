package es.atomsusej.tareaBackend.unit.business

import es.atomsusej.tareaBackend.business.AsignaturaBusisness
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Asignatura
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.util.Optional

// Aquí indico que voy a usar Mockito junto con JUnit 5 para simular dependencias
@ExtendWith(MockitoExtension::class)
class AsignaturaBusisnessTest {

    // Aquí simulo el repositorio para que el test no use la base de datos real
    @Mock
    lateinit var asignaturaRepository: AsignaturaRepository

    // Aquí declaro manualmente la clase de negocio
    // No uso @InjectMocks porque en la clase real el repositorio está declarado como val
    // y Mockito no lo inyecta bien al ser un campo final
    lateinit var asignaturaBusiness: AsignaturaBusisness

    @BeforeEach
    fun setUp() {
        // Aquí creo yo mismo la instancia del business
        asignaturaBusiness = AsignaturaBusisness()

        // Aquí inyecto el mock usando reflection para evitar el problema del val
        ReflectionTestUtils.setField(asignaturaBusiness, "AsignaturaRepository", asignaturaRepository)
    }

    @Test
    fun getAsignaturaRepository() {
        // Aquí compruebo que la clase se ha creado correctamente
        assertNotNull(asignaturaBusiness)

        // Aquí compruebo que el repositorio quedó inyectado correctamente
        assertNotNull(ReflectionTestUtils.getField(asignaturaBusiness, "AsignaturaRepository"))
    }

    @Test
    fun list() {
        // Aquí preparo dos asignaturas de ejemplo
        val asignatura1 = Asignatura("Matemáticas", "Mañana", 1)
        asignatura1.id_asignatura = 1

        val asignatura2 = Asignatura("Lengua", "Tarde", 2)
        asignatura2.id_asignatura = 2

        val listaEsperada = listOf(asignatura1, asignatura2)

        // Aquí simulo que el repositorio devuelve la lista de asignaturas
        `when`(asignaturaRepository.findAll()).thenReturn(listaEsperada)

        // Aquí ejecuto el método que quiero probar
        val resultado = asignaturaBusiness.list()

        // Aquí compruebo que devuelve exactamente la lista esperada
        assertEquals(2, resultado.size)
        assertEquals(listaEsperada, resultado)

        // Aquí verifico que findAll se llamó una sola vez
        verify(asignaturaRepository, times(1)).findAll()
    }

    @Test
    fun load() {
        // Aquí preparo una asignatura concreta
        val asignatura = Asignatura("Matemáticas", "Mañana", 1)
        asignatura.id_asignatura = 1

        // Aquí simulo que al buscar por id la asignatura existe
        `when`(asignaturaRepository.findById(1)).thenReturn(Optional.of(asignatura))

        // Aquí ejecuto la carga
        val resultado = asignaturaBusiness.load(1)

        // Aquí compruebo que devuelve la asignatura esperada
        assertEquals(asignatura, resultado)

        // Aquí verifico que findById se ejecutó una vez
        verify(asignaturaRepository, times(1)).findById(1)
    }

    @Test
    fun save() {
        // Aquí preparo una asignatura válida
        val asignatura = Asignatura("Matemáticas", "Mañana", 1)
        asignatura.id_asignatura = 1

        // Aquí simulo que el repositorio guarda la asignatura y la devuelve
        `when`(asignaturaRepository.save(asignatura)).thenReturn(asignatura)

        // Aquí ejecuto el guardado desde la capa de negocio
        val resultado = asignaturaBusiness.save(asignatura)

        // Aquí compruebo que devuelve el mismo objeto esperado
        assertEquals(asignatura, resultado)

        // Aquí verifico que save se llamó una sola vez
        verify(asignaturaRepository, times(1)).save(asignatura)
    }

    @Test
    fun remove() {
        // Aquí preparo una asignatura existente
        val asignatura = Asignatura("Matemáticas", "Mañana", 1)
        asignatura.id_asignatura = 1

        // Aquí simulo que la asignatura existe
        `when`(asignaturaRepository.findById(1)).thenReturn(Optional.of(asignatura))

        // Aquí indico que el borrado no debe fallar
        doNothing().`when`(asignaturaRepository).deleteById(1)

        // Aquí ejecuto el borrado
        asignaturaBusiness.remove(1)

        // Aquí verifico que primero buscó la asignatura
        verify(asignaturaRepository, times(1)).findById(1)

        // Aquí verifico que después la borró
        verify(asignaturaRepository, times(1)).deleteById(1)
    }

    @Test
    fun load_asignaturaNoEncontrada_lanzaExcepcion() {
        // Aquí simulo que la asignatura no existe
        `when`(asignaturaRepository.findById(99)).thenReturn(Optional.empty())

        // Aquí compruebo que cuando no existe, la excepción correcta es NotFoundException
        // y no BusinessException, porque el repositorio no falla: simplemente no encuentra datos
        assertThrows(NotFoundException::class.java) {
            asignaturaBusiness.load(99)
        }

        // Aquí verifico que sí se intentó buscar
        verify(asignaturaRepository, times(1)).findById(99)
    }

    @Test
    fun remove_asignaturaNoExiste_lanzaExcepcion() {
        // Aquí simulo que la asignatura no existe
        `when`(asignaturaRepository.findById(99)).thenReturn(Optional.empty())

        // Aquí compruebo que remove() lanza NotFoundException cuando no encuentra la asignatura
        assertThrows(NotFoundException::class.java) {
            asignaturaBusiness.remove(99)
        }

        // Aquí verifico que sí se intentó buscar
        verify(asignaturaRepository, times(1)).findById(99)

        // Aquí verifico que nunca intentó borrar porque no existía
        verify(asignaturaRepository, never()).deleteById(99)
    }
}

