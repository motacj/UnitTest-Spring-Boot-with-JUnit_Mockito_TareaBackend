package es.atomsusej.tareaBackend.unit.models

import es.atomsusej.tareaBackend.models.MatriculaId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.io.Serializable

class MatriculaIdUnitTest {

    @Test
    fun crear_matriculaId_con_datos_correctos() {
        // Aquí creo una clave compuesta con sus dos ids
        val matriculaId = MatriculaId(
            id_asignatura = 10,
            id_alumno = 20
        )

        // Aquí compruebo que los valores se han guardado correctamente
        assertEquals(10, matriculaId.id_asignatura)
        assertEquals(20, matriculaId.id_alumno)
    }

    @Test
    fun crear_matriculaId_con_constructor_vacio() {
        // Aquí creo la clave compuesta con el constructor vacío
        val matriculaId = MatriculaId()

        // Aquí compruebo que los valores por defecto son correctos
        assertEquals(0, matriculaId.id_asignatura)
        assertEquals(0, matriculaId.id_alumno)
    }

    @Test
    fun matriculaId_implementa_serializable() {
        // Aquí creo una clave compuesta
        val matriculaId = MatriculaId()

        // Aquí compruebo que implementa Serializable, que es obligatorio para claves compuestas
        assertEquals(true, matriculaId is Serializable)
    }

    @Test
    fun matriculaId_no_es_nulo_al_crearse() {
        // Aquí creo la clave compuesta
        val matriculaId = MatriculaId()

        // Aquí compruebo que el objeto se ha creado correctamente
        assertNotNull(matriculaId)
    }
}