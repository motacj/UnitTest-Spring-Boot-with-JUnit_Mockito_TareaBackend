package es.atomsusej.tareaBackend.unit.util

import es.atomsusej.tareaBackend.utils.ConstantsApi
import es.atomsusej.tareaBackend.utils.ConstantsAsignaturas
import es.atomsusej.tareaBackend.utils.ConstantsAutenticarthor
import es.atomsusej.tareaBackend.utils.ConstantsMatriculas
import es.atomsusej.tareaBackend.utils.ConstantsPersonas
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstantsTest {

    @Test
    fun constantsApi_tiene_valores_correctos() {
        // Aquí compruebo que la base general de la API es correcta
        assertEquals("/api", ConstantsApi.URL_API_BASE)
        assertEquals("/api", ConstantsApi.URL_BASE)
    }

    @Test
    fun constantsPersonas_tiene_valores_correctos() {
        // Aquí compruebo que la ruta base de personas está bien construida
        assertEquals("/v1", ConstantsPersonas.URL_API_VERSION)
        assertEquals("/api/v1/personas", ConstantsPersonas.URL_BASE_PERSONAS)
    }

    @Test
    fun constantsAsignaturas_tiene_valores_correctos() {
        // Aquí compruebo que la ruta base de asignaturas está bien construida
        assertEquals("/v2", ConstantsAsignaturas.URL_API_VERSION)
        assertEquals("/api/v2/asignaturas", ConstantsAsignaturas.URL_BASE_ASIGNATURAS)
    }

    @Test
    fun constantsMatriculas_tiene_valores_correctos() {
        // Aquí compruebo que la ruta base de matrículas está bien construida
        assertEquals("/v3", ConstantsMatriculas.URL_API_VERSION)
        assertEquals("/api/v3/matriculas", ConstantsMatriculas.URL_BASE_MATRICULAS)
    }

    @Test
    fun constantsAuth_tiene_valores_correctos() {
        // Aquí compruebo que la ruta base de autenticación está bien construida
        assertEquals("/auth", ConstantsAutenticarthor.URL_API_VERSION)
        assertEquals("/api/auth", ConstantsAutenticarthor.URL_BASE_AUTH)
    }
}