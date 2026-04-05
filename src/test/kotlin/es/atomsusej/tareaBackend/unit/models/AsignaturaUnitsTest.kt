package es.atomsusej.tareaBackend.unit.models

import es.atomsusej.tareaBackend.models.Asignatura
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class AsignaturaUnitsTest {

    @Test
    fun crear_asignatura_con_datos_correctos() {
        // Aquí creo una asignatura con todos sus datos
        val asignatura = Asignatura(
            nombre_asignatura = "Matemáticas",
            horario = "Mañana",
            id_profesor = 1
        )
        asignatura.id_asignatura = 100

        // Aquí compruebo que los valores se han guardado correctamente
        assertEquals(100, asignatura.id_asignatura)
        assertEquals("Matemáticas", asignatura.nombre_asignatura)
        assertEquals("Mañana", asignatura.horario)
        assertEquals(1, asignatura.id_profesor)
    }

    @Test
    fun crear_asignatura_con_constructor_vacio() {
        // Aquí creo una asignatura con el constructor vacío por defecto
        val asignatura = Asignatura()

        // Aquí compruebo que los valores iniciales son correctos
        assertEquals(0, asignatura.id_asignatura)
        assertEquals("", asignatura.nombre_asignatura)
        assertEquals("", asignatura.horario)
        assertEquals(0, asignatura.id_profesor)
    }

    @Test
    fun asignatura_permite_modificar_id() {
        // Aquí creo una asignatura y luego le asigno un id manualmente
        val asignatura = Asignatura(
            nombre_asignatura = "Historia",
            horario = "Tarde",
            id_profesor = 2
        )

        asignatura.id_asignatura = 200

        // Aquí compruebo que el id se ha actualizado correctamente
        assertEquals(200, asignatura.id_asignatura)
    }

    @Test
    fun asignatura_no_es_nula_al_crearse() {
        // Aquí creo una asignatura
        val asignatura = Asignatura()

        // Aquí compruebo que el objeto se ha creado correctamente
        assertNotNull(asignatura)
    }
}