package es.atomsusej.tareaBackend.models


import jakarta.persistence.*

//Esta entidad coincide con una de las tablas de nuestra BBDD
//En Kotlin si inicializamos las variables del constructor, define un constructor vacio
//Como la tabla en la BBDD tiene columnas id_asignatura y id_persona, JPA las mapeará automáticamente desde MatriculaId.
@Entity
@Table(name = "Matricula")
data class Matricula(

    @EmbeddedId
    var id: MatriculaId = MatriculaId(),

    @Column(name = "nota")
    var nota: Float = 0.0f
)