package es.atomsusej.tareaBackend.utils

class ConstantsApi {
    companion object {
        const val URL_API_BASE = "/api"

        const val URL_BASE = "$URL_API_BASE"
    }
}

class ConstantsPersonas {
    companion object {
        const val URL_API_VERSION = "/v1"
        const val URL_BASE_PERSONAS = "${ConstantsApi.URL_BASE}$URL_API_VERSION/personas"
    }
}

class ConstantsAsignaturas {
    companion object {
        const val URL_API_VERSION = "/v2"
        const val URL_BASE_ASIGNATURAS = "${ConstantsApi.URL_BASE}$URL_API_VERSION/asignaturas"
    }
}

class ConstantsMatriculas {
    companion object {
        const val URL_API_VERSION = "/v3"
        const val URL_BASE_MATRICULAS = "${ConstantsApi.URL_BASE}$URL_API_VERSION/matriculas"
    }
}

class ConstantsAutenticarthor {
    companion object {
        const val URL_API_VERSION = "/auth"
        const val URL_BASE_AUTH = "${ConstantsApi.URL_BASE}$URL_API_VERSION"
    }
}