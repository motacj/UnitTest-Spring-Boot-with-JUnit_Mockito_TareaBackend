package es.atomsusej.tareaBackend.web

// Importo las comprobaciones que voy a usar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

// Importo la anotación @Test para marcar el método de prueba
import org.junit.jupiter.api.Test

// Importo la clase User de Spring Security porque implementa UserDetails
import org.springframework.security.core.userdetails.User

class TestControllerTest {

    // Aquí creo una instancia directa del controlador que quiero probar
    private val controller = TestController()

    @Test
    fun secure() {
        // Aquí creo un usuario simulado para pasárselo al método secure
        // Uso la clase User de Spring Security porque ya implementa UserDetails
        val user = User
            .withUsername("Jesus")
            .password("1234")
            .authorities(emptyList())
            .build()

        // Aquí ejecuto el método secure pasando el usuario simulado
        val resultado = controller.secure(user)

        // Compruebo que el resultado no sea nulo
        assertNotNull(resultado)

        // Aquí comparo el valor esperado con el valor real que devuelve el método
        assertEquals("Entraste con JWT. Usuario: Jesus", resultado)
    }
}