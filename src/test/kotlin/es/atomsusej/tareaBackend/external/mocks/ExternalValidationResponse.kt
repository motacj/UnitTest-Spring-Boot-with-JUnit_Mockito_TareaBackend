package es.atomsusej.tareaBackend.external

data class ExternalValidationResponse(
    val valid: Boolean = false,
    val message: String = ""
)