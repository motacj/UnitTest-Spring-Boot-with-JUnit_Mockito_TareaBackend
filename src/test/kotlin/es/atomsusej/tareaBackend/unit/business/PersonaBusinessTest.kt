package es.atomsusej.tareaBackend.unit.business

import es.atomsusej.tareaBackend.business.PersonaBusiness
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Persona
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
class PersonaBusinessTest {

    // Aquí simulo el repositorio para que el test no use la base de datos real
    @Mock
    lateinit var personaRepository: PersonaRepository

    // Elimino @InjectMocks porque el campo PersonaRepository en
    // PersonaBusiness es "val" (final (constante) en java) y según Mockito 5 NO puede inyectar
    // en campos finales. En su lugar lo creamos manualmente en @BeforeEach.
    lateinit var personaBusiness: PersonaBusiness

    @BeforeEach
    fun setUp() {
        // Creamos la instancia manualmente
        personaBusiness = PersonaBusiness()
        // Inserto el mock vía ReflectionTestUtils,
        // que sí puede acceder a campos privados/finales en clases de usuario.
        // El nombre del campo debe coincidir exactamente con el de la clase: "PersonaRepository"
        ReflectionTestUtils.setField(personaBusiness, "PersonaRepository", personaRepository)
    }

    @Test
    fun getPersonaRepository() {
        // Aquí compruebo que la clase de negocio se ha creado correctamente
        // y que el repositorio fue inyectado (no es null)
        assertNotNull(personaBusiness)
        assertNotNull(ReflectionTestUtils.getField(personaBusiness, "PersonaRepository"))
    }

    @Test
    fun list() {
        // Aquí preparo dos personas de ejemplo para simular que existen en el repositorio
        val persona1 = Persona(1, "Juan", "Pérez", 30)
        val persona2 = Persona(2, "Ana", "López", 25)
        val listaEsperada = listOf(persona1, persona2)

        // Aquí le digo al mock que cuando se llame a findAll devuelva mi lista de prueba
        `when`(personaRepository.findAll()).thenReturn(listaEsperada)

        // Ejecuto el método que quiero comprobar
        val resultado = personaBusiness.list()

        // Aquí compruebo que el resultado tiene 2 elementos y coincide con la lista esperada
        assertEquals(2, resultado.size)
        assertEquals(listaEsperada, resultado)

        // Aquí verifico que realmente se ha llamado una vez al repositorio
        verify(personaRepository, times(1)).findAll()
    }

    @Test
    fun load() {
        // Aquí preparo una persona para simular que existe en la base de datos
        val persona = Persona(1, "Juan", "Pérez", 30)

        // Aquí simulo que al buscar por id 1 el repositorio encuentra esa persona
        `when`(personaRepository.findById(1)).thenReturn(Optional.of(persona))

        // Aquí llamo al metodo que carga una persona con su ID
        val resultado = personaBusiness.load(1)

        // Aquí compruebo que devuelve exactamente la persona esperada
        assertEquals(persona, resultado)

        // Aquí verifico que la búsqueda por id se ha ejecutado una vez
        verify(personaRepository, times(1)).findById(1)
    }

    @Test
    fun save() {
        // Aquí preparo una persona válida que quiero guardar
        val persona = Persona(1, "Juan", "Pérez", 30)

        // Aquí simulo que el repositorio guarda la persona y la devuelve
        `when`(personaRepository.save(persona)).thenReturn(persona)

        // Aquí ejecuto el guardado desde la capa de negocio
        val resultado = personaBusiness.save(persona)

        // Aquí compruebo que el objeto devuelto es el mismo que esperaba
        assertEquals(persona, resultado)

        // Aquí el método save se comprueba que solo se llama una vez
        verify(personaRepository, times(1)).save(persona)
    }

    @Test
    fun remove() {
        val persona = Persona(1, "Juan", "Pérez", 30)


        // Simulamos findById para que devuelva la persona existente.
        `when`(personaRepository.findById(1)).thenReturn(Optional.of(persona))

        // Aquí indico que al borrar no debe pasar nada extraño
        doNothing().`when`(personaRepository).deleteById(1)

        // Aquí ejecuto el borrado desde la capa de negocio
        personaBusiness.remove(1)

        // Verifico que existe el findById
        verify(personaRepository, times(1)).findById(1)

        // Aquí verifico que después se llamó al borrado
        verify(personaRepository, times(1)).deleteById(1)
    }

    @Test
    fun load_personaNoEncontrada_lanzaExcepcion() {
        // Aquí simulo que el repositorio no encuentra ninguna persona con id 99
        `when`(personaRepository.findById(99)).thenReturn(Optional.empty())

        // El código lanza NotFoundException (no BusinessException) cuando
        // la persona no existe. BusinessException solo se lanza si falla el propio
        // repositorio (bloque catch de Exception). Ver PersonaBusiness.load().
        assertThrows(NotFoundException::class.java) {
            personaBusiness.load(99)
        }

        // Aquí verifico que sí se intentó buscar esa persona
        verify(personaRepository, times(1)).findById(99)
    }

    @Test
    fun remove_personaNoExiste_lanzaExcepcion() {

        // Cuando no encuentra la persona lanza NotFoundException, no BusinessException.
        `when`(personaRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            personaBusiness.remove(99)
        }

        // Aquí verifico que se llamó a findById (no existsById)
        verify(personaRepository, times(1)).findById(99)

        // Aquí verifico que nunca se intentó borrar porque no existía
        verify(personaRepository, never()).deleteById(99)
    }
}
