package es.atomsusej.tareaBackend.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

//Esta entidad coincide con una de las tablas de nuestra BBDD
//En Kotlin si inicializamos las variables del constructor, define un constructor vacio
@Entity
@Table(name = "Persona")
data class Persona(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_persona:Int = 0,
    @Column(name = "nombre")
    var nombre: String = "",
    @Column(name = "apellidos")
    var apellidos: String = "",
    @Column(name = "edad")
    var edad: Int = 0) {
}