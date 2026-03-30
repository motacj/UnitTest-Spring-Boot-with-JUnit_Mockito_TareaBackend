package es.atomsusej.tareaBackend.web


import es.atomsusej.tareaBackend.business.IPersonaBusisness
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Persona
import es.atomsusej.tareaBackend.utils.ConstantsPersonas
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod
@CrossOrigin(
    origins = ["http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:3001", "http://127.0.0.1:3001"],
    methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE]
)



@RestController
@RequestMapping(ConstantsPersonas.URL_BASE_PERSONAS)
open class PersonaRestController {
    //Da valor a una variable que extiende de la Interface y por tanto puede utilizar todos sus metodos
    //Los cuales los hemos implementado para que nos den respuesta
    @Autowired
    open val personaBusiness : IPersonaBusisness? = null

    //Lee y nos devuelve una lista de personas
    @GetMapping("")
    open fun list(): ResponseEntity<List<Persona>>{
        return try {
            ResponseEntity(personaBusiness!!.list(), HttpStatus.OK)
        }catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Leemos un usuario
    @GetMapping("/{id}")
    open fun load(@PathVariable("id") idPersona:Int): ResponseEntity<Any>{
        return try {
            ResponseEntity(personaBusiness!!.load(idPersona), HttpStatus.OK)
        }catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //Cargamos un usuario
    //Any hace que nos devuevla un objeto de cualquier tipo sea persona u otro
    @PostMapping("")
    open fun insert(@RequestBody persona: Persona): ResponseEntity<Any>{
        return try {
            personaBusiness!!.save(persona)
            val responseHeader = HttpHeaders()
            responseHeader.set("location", ConstantsPersonas.URL_BASE_PERSONAS + "/" + persona.id_persona)
            ResponseEntity(responseHeader, HttpStatus.CREATED)
        }catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Actualizamos un campo
    @PutMapping("")
    open fun update(@RequestBody persona: Persona): ResponseEntity<Any> {
        return try {
            personaBusiness!!.save(persona)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Borrar un registro
    @DeleteMapping("/{id}")
    open fun delete(@PathVariable("id") idPersona: Int): ResponseEntity<Any>{
        return try {
            personaBusiness!!.remove(idPersona)
            ResponseEntity(HttpStatus.OK)
        }catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
