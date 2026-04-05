package es.atomsusej.tareaBackend.external

class ExternalValidationService(
    private val externalValidationClient: ExternalValidationClient
) {

    fun validate(document: String): String {
        return try {
            val response = externalValidationClient.validateDocument(document)

            if (response == null) {
                "Respuesta vacía del servicio externo"
            } else if (response.valid) {
                "Validación correcta: ${response.message}"
            } else {
                "Validación incorrecta: ${response.message}"
            }

        } catch (e: RuntimeException) {
            "Error controlado al validar con servicio externo"
        }
    }
}