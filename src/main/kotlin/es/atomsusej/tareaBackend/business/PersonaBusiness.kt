package es.atomsusej.tareaBackend.business

import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.exception.BusinessException
import es.atomsusej.tareaBackend.exception.NotFoundException
import es.atomsusej.tareaBackend.models.Persona
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.jvm.Throws
import java.util.Optional

//Va ha se la encargarda de implementar la inteface IPersonaBussines
//POr tanto la declararemos con un Servicio y ara las consultas a la BBDD

@Service
class PersonaBusiness: IPersonaBusisness {
    //sirve para que Spring te dé automáticamente un objeto ya creado, sin que tú tengas que usar new
    @Autowired
    val PersonaRepository: PersonaRepository?=null

    @Throws(BusinessException::class)
    override fun list(): List<Persona> {
        try {
            return PersonaRepository!!.findAll()
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun load(id_persona: Int): Persona {
        //Para saber si este dato esta en la BBDD sino retornara una Exception
        val op: Optional<Persona>
        try {
            op = PersonaRepository!!.findById(id_persona)
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("No se encontro la Persona con id $id_persona")
        }
        return op.get()
    }
    @Throws(BusinessException::class)
    override fun save(persona: Persona): Persona {
        try {
            return PersonaRepository!!.save(persona)
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun remove(id_persona: Int) {
        //Para saber si este dato esta en la BBDD sino retornara una Exception
        val op: Optional<Persona> = try {
            PersonaRepository!!.findById(id_persona)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }

        if (!op.isPresent) {
            throw NotFoundException("No se encontró la Persona con id $id_persona")
        }

        try {
            PersonaRepository!!.deleteById(id_persona)
        } catch (e: Exception) {
            throw BusinessException(e.message)
        }
    }
}