package es.atomsusej.tareaBackend.config

// Importo las comprobaciones de JUnit 5 que voy a usar en mis pruebas
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

// Importo la anotación @Test para indicar qué métodos son pruebas
import org.junit.jupiter.api.Test

// Importo esta clase para poder acceder a la configuración CORS registrada
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

// Importo el filtro CORS que quiero probar
import org.springframework.web.filter.CorsFilter

// Importo una petición simulada para no tener que arrancar toda la aplicación
import org.springframework.mock.web.MockHttpServletRequest

// En esta clase voy a probar la configuración de WebConfig
class WebConfigTest {

    // Aquí creo una instancia directa de mi clase WebConfig
    // Lo hago así porque esta prueba es unitaria y no necesito levantar Spring Boot entero
    private val webConfig = WebConfig()

    @Test
    fun `debo comprobar que se crea el bean CorsFilter`() {
        // Llamo al método corsFilter() para crear el filtro
        val corsFilter: CorsFilter = webConfig.corsFilter()

        // Compruebo que el filtro no sea nulo
        // Si no es nulo, significa que el bean se crea correctamente
        assertNotNull(corsFilter)
    }

    @Test
    fun `debo comprobar que la configuracion CORS es correcta`() {
        // Vuelvo a crear el filtro para analizar su configuración interna
        val corsFilter = webConfig.corsFilter()

        // Aquí uso reflexión para acceder al campo privado configSource del filtro
        // Lo hago porque necesito leer la configuración que se ha registrado dentro
        val field = CorsFilter::class.java.getDeclaredField("configSource")
        field.isAccessible = true

        // Convierto ese campo al tipo UrlBasedCorsConfigurationSource
        // Así puedo consultar la configuración asociada a una ruta concreta
        val source = field.get(corsFilter) as UrlBasedCorsConfigurationSource

        // Creo una petición simulada tipo GET a una ruta cualquiera
        // Me sirve para pedirle a Spring la configuración CORS que aplicaría
        val request = MockHttpServletRequest("GET", "/personas")

        // Recupero la configuración CORS correspondiente a esa petición
        val config = source.getCorsConfiguration(request)

        // Compruebo que la configuración exista
        assertNotNull(config)

        // Compruebo que se permita enviar credenciales
        // Esto es importante para cookies o cabeceras de autorización
        assertTrue(config!!.allowCredentials == true)

        // Compruebo que los orígenes permitidos sean los que he definido en mi clase WebConfig
        assertEquals(
            listOf("http://localhost:[*]", "http://127.0.0.1:[*]"),
            config.allowedOriginPatterns
        )

        // Compruebo que los métodos HTTP permitidos sean los esperados
        assertEquals(
            listOf("GET", "POST", "PUT", "DELETE", "OPTIONS"),
            config.allowedMethods
        )

        // Compruebo que se permitan todas las cabeceras
        assertEquals(
            listOf("*"),
            config.allowedHeaders
        )
    }
}