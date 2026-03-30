package es.atomsusej.tareaBackend.business

import es.atomsusej.tareaBackend.models.Matricula

interface IMatriculaBusisness {
    //Funcion qeu retorna una lista de matriculas
    fun list(): List<Matricula>
    //Funcion que nos devuelve una Matricula segun su id_asignatura y su id_alumno
    fun load(id_asignatura: Int, id_alumno : Int) : Matricula
    //Guardar una Matricula
    fun save(matricula: Matricula) : Matricula
    //Borrado de datos
    fun remove(id_asignatura: Int, id_alumno : Int)
}