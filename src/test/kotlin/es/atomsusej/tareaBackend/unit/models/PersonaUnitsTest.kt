package es.atomsusej.tareaBackend.unit.models

import es.atomsusej.tareaBackend.models.Persona
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class PersonaUnitsTest {

    @Test
    fun crear_persona_con_datos_correctos() {
        // Aquí creo una persona con todos sus datos
        val persona = Persona(
            id_persona = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            edad = 30
        )

        // Aquí compruebo que los valores se han guardado correctamente en el objeto
        assertEquals(1, persona.id_persona)
        assertEquals("Juan", persona.nombre)
        assertEquals("Pérez", persona.apellidos)
        assertEquals(30, persona.edad)
    }

    @Test
    fun crear_persona_con_constructor_vacio() {
        // Aquí creo una persona usando el constructor vacío por defecto
        val persona = Persona()

        // Aquí compruebo que se crean los valores iniciales por defecto
        assertEquals(0, persona.id_persona)
        assertEquals("", persona.nombre)
        assertEquals("", persona.apellidos)
        assertEquals(0, persona.edad)
    }

    @Test
    fun persona_data_class_compara_correctamente() {
        // Aquí creo dos personas con los mismos datos
        val persona1 = Persona(1, "Ana", "López", 25)
        val persona2 = Persona(1, "Ana", "López", 25)

        // Aquí compruebo que al ser data class ambas personas son iguales
        assertEquals(persona1, persona2)
    }

    @Test
    fun persona_no_es_nula_al_crearse() {
        // Aquí creo una persona
        val persona = Persona()

        // Aquí compruebo que el objeto se ha creado correctamente
        assertNotNull(persona)
    }
}