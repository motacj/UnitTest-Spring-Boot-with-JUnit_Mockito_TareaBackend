package es.atomsusej.tareaBackend.dao

import es.atomsusej.tareaBackend.models.MatriculaId
import es.atomsusej.tareaBackend.models.Matricula
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

//esta interface se va ha encargar de la manipulacion de los datos (DML) en la BBDD
//Extendemos de JpaRepository que tiene varios metodos de DML
@Repository
interface MatriculaRepository : JpaRepository<Matricula, MatriculaId> {
}