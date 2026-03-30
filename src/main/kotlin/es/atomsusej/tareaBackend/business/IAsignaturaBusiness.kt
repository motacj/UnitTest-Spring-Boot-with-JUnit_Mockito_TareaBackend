package es.atomsusej.tareaBackend.business


import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Persona

//Sirve para declarar los metodos que se utilizaran en PersonaBusiness
interface IAsignaturaBusiness {
    //Funcion qeu retorna una lista de personas
    fun list(): List<Asignatura>
    //Funcion que nos devuelve una Persona segun su ID
    fun load(id_asignatura: Int) : Asignatura
    //Guardar una Persona
    fun save(asignatura: Asignatura) : Asignatura
    //Borrado de datos
    fun remove(id_asignatura: Int)
}