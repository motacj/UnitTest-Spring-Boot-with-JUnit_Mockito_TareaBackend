package es.atomsusej.tareaBackend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class UsersConfig {

    // 1) Codificador de contraseña (para no guardar "1234" en plano)
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    // 2) Usuarios en memoria (para pruebas)
    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService {

        // Usuario admin / 1234
        val admin = User.builder()
            .username("admin")
            .password(encoder.encode("1234")) // se guarda cifrada
            .roles("ADMIN")
            .build()

        // Usuario user / 1234
        val user = User.builder()
            .username("user")
            .password(encoder.encode("1234"))
            .roles("USER")
            .build()

        // Spring Security buscará usuarios aquí cuando alguien haga login
        return InMemoryUserDetailsManager(admin, user)
    }
}