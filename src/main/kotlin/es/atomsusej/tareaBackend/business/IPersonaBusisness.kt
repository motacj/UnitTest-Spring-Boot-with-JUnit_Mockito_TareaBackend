package es.atomsusej.tareaBackend.business

import es.atomsusej.tareaBackend.models.Persona

//Sirve para declarar los metodos que se utilizaran en PersonaBusiness
interface IPersonaBusisness {
    //Funcion qeu retorna una lista de personas
    fun list(): List<Persona>
    //Funcion que nos devuelve una Persona segun su ID
    fun load(id_persona: Int) : Persona
    //Guardar una Persona
    fun save(persona: Persona) : Persona
    //Borrado de datos
    fun remove(id_persona: Int)
}