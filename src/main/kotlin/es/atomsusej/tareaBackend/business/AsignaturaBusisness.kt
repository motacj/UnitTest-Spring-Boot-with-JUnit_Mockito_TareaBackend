package es.atomsusej.tareaBackend.business

import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Persona
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional


//Va ha se la encargarda de implementar la inteface IAsiganturaBusisnes
@Service
class AsignaturaBusisness : IAsignaturaBusiness{

    //sirve para que Spring te dé automáticamente un objeto ya creado, sin que tú tengas que usar new
    @Autowired
    val AsignaturaRepository: AsignaturaRepository?=null

    @Throws(BusinessException::class)
    override fun list(): List<Asignatura> {
        try {
            return AsignaturaRepository!!.findAll()
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }
    @Throws(BusinessException::class, NotFoundException::class)
    override fun load(id_asignatura: Int): Asignatura {
        val op: Optional<Asignatura>
        try {
            op = AsignaturaRepository!!.findById(id_asignatura)
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("No se encontro la Persona con id $id_asignatura")
        }
        return op.get()
    }
    @Throws(BusinessException::class)
    override fun save(asignatura: Asignatura): Asignatura {
        try {
            return AsignaturaRepository!!.save(asignatura)
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun remove(id_asignatura: Int) {
        val op: Optional<Asignatura>
        try {
            op = AsignaturaRepository!!.findById(id_asignatura)
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("No se encontro la Persona con id $id_asignatura")
        }else{
            try{
                AsignaturaRepository!!.deleteById(id_asignatura)
            }catch (e: Exception){
                throw BusinessException(e.message)
            }
        }
    }


}