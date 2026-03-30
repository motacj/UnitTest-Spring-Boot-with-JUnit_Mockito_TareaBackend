package es.atomsusej.tareaBackend.business

import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.dao.MatriculaRepository
import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

//Va ha se la encargarda de implementar la inteface IMatriculasBusisnes
@Service
class MatriculaBusisness : IMatriculaBusisness{

    //sirve para que Spring te dé automáticamente un objeto ya creado, sin que tú tengas que usar new
    @Autowired
    val MatriculaRepository: MatriculaRepository?=null
    @Autowired
    val asignaturaRepository: AsignaturaRepository? = null  // NUEVO
    @Autowired
    val alumnoRepository: PersonaRepository? = null

    override fun list(): List<Matricula> {
        try {
            return MatriculaRepository!!.findAll()
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    override fun load(id_asignatura: Int, id_alumno: Int): Matricula {
        //Para saber si este dato esta en la BBDD sino retornara una Exception
        val pk = MatriculaId(id_asignatura, id_alumno)

        val op: Optional<Matricula> = try {
            MatriculaRepository!!.findById(pk)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }

        if (!op.isPresent) {
            throw NotFoundException(
                "No se encontró la matrícula con id_asignatura=$id_asignatura e id_alumno=$id_alumno"
            )
        }

        return op.get()
    }

    override fun save(matricula: Matricula): Matricula {

        // 1. Sacamos los IDs de la clave compuesta
        val idAsignatura = matricula.id.id_asignatura  // o idAsignatura según tu data class
        val idAlumno = matricula.id.id_alumno          // o idAlumno

        // 2. Comprobamos que existe la Asignatura
        val asigOp = try {
            asignaturaRepository!!.findById(idAsignatura)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }

        if (!asigOp.isPresent) {
            throw NotFoundException("No existe la Asignatura con id $idAsignatura")
        }

        // 3. Comprobamos que existe el Alumno
        val alumOp = try {
            alumnoRepository!!.findById(idAlumno)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }

        if (!alumOp.isPresent) {
            throw NotFoundException("No existe el Alumno con id $idAlumno")
        }

        // 4. Si todo existe, guardamos la matrícula
        try {
            return MatriculaRepository!!.save(matricula)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }
    }

    override fun remove(id_asignatura: Int, id_alumno: Int) {
        val pk = MatriculaId(id_asignatura, id_alumno)

        val op: Optional<Matricula> = try {
            MatriculaRepository!!.findById(pk)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }

        if (!op.isPresent) {
            throw NotFoundException(
                "No se encontró la matrícula con id_asignatura=$id_asignatura e id_alumno=$id_alumno"
            )
        }

        try {
            MatriculaRepository!!.deleteById(pk)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }
    }
}