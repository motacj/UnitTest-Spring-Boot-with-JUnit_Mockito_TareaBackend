package es.atomsusej.tareaBackend.models

import jakarta.persistence.Embeddable
import java.io.Serializable

//Indica que esta clase se pued usar como clave
@Embeddable
class MatriculaId(
    var id_asignatura: Int = 0,
    var id_alumno: Int = 0
) : Serializable
//Obligatorio para claves compuestas