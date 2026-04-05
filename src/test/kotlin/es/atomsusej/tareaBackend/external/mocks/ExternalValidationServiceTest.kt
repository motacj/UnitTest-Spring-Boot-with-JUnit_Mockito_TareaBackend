package es.atomsusej.tareaBackend.external.mocks

import es.atomsusej.tareaBackend.external.ExternalValidationClient
import es.atomsusej.tareaBackend.external.ExternalValidationResponse
import es.atomsusej.tareaBackend.external.ExternalValidationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ExternalValidationServiceTest {

    @Mock
    lateinit var externalValidationClient: ExternalValidationClient

    @Test
    fun respuesta_correcta() {
        val service = ExternalValidationService(externalValidationClient)

        `when`(externalValidationClient.validateDocument("12345678A"))
            .thenReturn(ExternalValidationResponse(true, "Documento válido"))

        val result = service.validate("12345678A")

        assertEquals("Validación correcta: Documento válido", result)
        verify(externalValidationClient, times(1)).validateDocument("12345678A")
    }

    @Test
    fun error_500_o_excepcion() {
        val service = ExternalValidationService(externalValidationClient)

        `when`(externalValidationClient.validateDocument("12345678A"))
            .thenThrow(RuntimeException("Error 500"))

        val result = service.validate("12345678A")

        assertEquals("Error controlado al validar con servicio externo", result)
        verify(externalValidationClient, times(1)).validateDocument("12345678A")
    }

    @Test
    fun timeout_o_excepcion() {
        val service = ExternalValidationService(externalValidationClient)

        `when`(externalValidationClient.validateDocument("12345678A"))
            .thenThrow(RuntimeException("Timeout"))

        val result = service.validate("12345678A")

        assertEquals("Error controlado al validar con servicio externo", result)
        verify(externalValidationClient, times(1)).validateDocument("12345678A")
    }

    @Test
    fun respuesta_vacia() {
        val service = ExternalValidationService(externalValidationClient)

        `when`(externalValidationClient.validateDocument("12345678A"))
            .thenReturn(null)

        val result = service.validate("12345678A")

        assertEquals("Respuesta vacía del servicio externo", result)
        verify(externalValidationClient, times(1)).validateDocument("12345678A")
    }
}