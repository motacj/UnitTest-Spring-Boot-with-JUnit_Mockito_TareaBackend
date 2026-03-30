package es.atomsusej.tareaBackend.web

import es.atomsusej.tareaBackend.business.IAsignaturaBusiness
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.utils.ConstantsAsignaturas
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ConstantsAsignaturas.Companion.URL_BASE_ASIGNATURAS)
open class AsignaturaRestController {

    @Autowired
    val asignaturaBusisness : IAsignaturaBusiness?=null

    @GetMapping("")
    open fun list() : ResponseEntity<List<Asignatura>> {
        return try{
            ResponseEntity(asignaturaBusisness!!.list(), HttpStatus.OK)
        }catch (e: Exception){
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{id}")
    open fun load(@PathVariable("id") idAsignatura: Int): ResponseEntity<Any> {
        return try{
            ResponseEntity(asignaturaBusisness!!.load(idAsignatura), HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("")
    open fun inset(@RequestBody asignatura: Asignatura): ResponseEntity<Any> {
        return try{
            asignaturaBusisness!!.save(asignatura)
            val responseHeader = HttpHeaders()
            responseHeader.set("location", ConstantsAsignaturas.Companion.URL_BASE_ASIGNATURAS + "/" + asignatura.id_asignatura)
            ResponseEntity(responseHeader, HttpStatus.CREATED)
        }catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("")
    open fun update(@RequestBody asignatura: Asignatura): ResponseEntity<Any> {
        return try{
            asignaturaBusisness!!.save(asignatura)
            ResponseEntity(HttpStatus.OK)
        }catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{id}")
    open fun delete(@PathVariable("id") idAsignatura: Int): ResponseEntity<Any> {
        return try {
            asignaturaBusisness!!.remove(idAsignatura)
            ResponseEntity(HttpStatus.OK)
        } catch (e: BusinessException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}