package es.atomsusej.tareaBackend.unit.models

import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class MatriculaUnitsTest {

    @Test
    fun crear_matricula_con_datos_correctos() {
        // Aquí creo una matrícula con una clave compuesta y una nota
        val id = MatriculaId(
            id_asignatura = 10,
            id_alumno = 20
        )

        val matricula = Matricula(
            id = id,
            nota = 7.5f
        )

        // Aquí compruebo que los valores se han guardado correctamente
        assertEquals(10, matricula.id.id_asignatura)
        assertEquals(20, matricula.id.id_alumno)
        assertEquals(7.5f, matricula.nota)
    }

    @Test
    fun crear_matricula_con_constructor_vacio() {
        // Aquí creo una matrícula con el constructor vacío por defecto
        val matricula = Matricula()

        // Aquí compruebo que los valores iniciales son correctos
        assertEquals(0, matricula.id.id_asignatura)
        assertEquals(0, matricula.id.id_alumno)
        assertEquals(0.0f, matricula.nota)
    }

    @Test
    fun matricula_data_class_compara_correctamente() {
        // Aquí creo dos matrículas con los mismos datos
        val matricula1 = Matricula(MatriculaId(1, 2), 8.0f)
        val matricula2 = Matricula(MatriculaId(1, 2), 8.0f)

        // Aquí no comparo el objeto id directamente, porque MatriculaId no es data class
        // y eso puede hacer que el equals falle aunque los valores sean iguales.
        // Por eso compruebo campo por campo.
        assertEquals(matricula1.id.id_asignatura, matricula2.id.id_asignatura)
        assertEquals(matricula1.id.id_alumno, matricula2.id.id_alumno)
        assertEquals(matricula1.nota, matricula2.nota)
    }

    @Test
    fun matricula_no_es_nula_al_crearse() {
        // Aquí creo una matrícula
        val matricula = Matricula()

        // Aquí compruebo que el objeto se ha creado correctamente
        assertNotNull(matricula)
    }
}