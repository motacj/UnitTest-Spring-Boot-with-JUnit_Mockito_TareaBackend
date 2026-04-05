package es.atomsusej.tareaBackend.external

interface ExternalValidationClient {
    fun validateDocument(document: String): ExternalValidationResponse?
}