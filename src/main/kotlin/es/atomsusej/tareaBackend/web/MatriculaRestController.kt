package es.atomsusej.tareaBackend.web


import es.atomsusej.tareaBackend.business.IMatriculaBusisness
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.utils.ConstantsMatriculas
import org.springframework.beans.factory.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ConstantsMatriculas.URL_BASE_MATRICULAS)
open class MatriculaRestController {

    @Autowired
    open val matriculaBusisness : IMatriculaBusisness?=null

    @GetMapping("")
    open fun list() : ResponseEntity<List<Matricula>> {
        return try{
            ResponseEntity(matriculaBusisness!!.list(), HttpStatus.OK)
        }catch (e: Exception){
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{id_asignatura}/{id_alumno}")
    open fun load(
        @PathVariable("id_asignatura") idAsignatura: Int,
        @PathVariable("id_alumno") idAlumno: Int
    ): ResponseEntity<Any> {
        return try {
            ResponseEntity(matriculaBusisness!!.load(idAsignatura, idAlumno), HttpStatus.OK)
        } catch (e: BusinessException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("")
    open fun insert(@RequestBody matricula: Matricula): ResponseEntity<Any> {
        return try {
            // Guardamos la matrícula
            val matriculaGuardada = matriculaBusisness!!.save(matricula)

            // Cabecera Location con la URL del nuevo recurso
            val responseHeader = HttpHeaders()
            responseHeader.set(
                "location",
                ConstantsMatriculas.URL_BASE_MATRICULAS + "/" +
                        matriculaGuardada.id.id_asignatura + "/" +
                        matriculaGuardada.id.id_alumno
            )

            ResponseEntity(responseHeader, HttpStatus.CREATED)
        } catch (e: BusinessException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("")
    open fun update(@RequestBody matricula: Matricula): ResponseEntity<Any> {
        return try {

            // Comprobar si la matrícula existe antes de actualizar
            val idAsignatura = matricula.id.id_asignatura
            val idAlumno = matricula.id.id_alumno

            // Si no existe, lanzará NotFoundException desde el business
            matriculaBusisness!!.load(idAsignatura, idAlumno)

            // Guardar la actualización
            matriculaBusisness!!.save(matricula)

            ResponseEntity(HttpStatus.OK)

        } catch (e: NotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)

        } catch (e: BusinessException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{id_alumno}/{id_asignatura}")
    open fun delete(
        @PathVariable("id_alumno") idAlumno: Int,
        @PathVariable("id_asignatura") idAsignatura: Int
    ): ResponseEntity<Any> {
        return try {
            // Aquí paso primero idAsignatura y después idAlumno,
            // porque así lo espera la capa de negocio
            matriculaBusisness!!.remove(idAsignatura, idAlumno)
            ResponseEntity(HttpStatus.OK)
        } catch (e: BusinessException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}