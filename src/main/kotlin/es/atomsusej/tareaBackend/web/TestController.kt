package es.atomsusej.tareaBackend.web

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping
    fun secure(@AuthenticationPrincipal user: UserDetails?): String {
        return if (user != null) {
            "Entraste con JWT. Usuario: ${user.username}"
        } else {
            "Usuario no autenticado"
        }
    }
}