package es.atomsusej.tareaBackend

import es.atomsusej.tareaBackend.dao.PersonaRepository
import es.atomsusej.tareaBackend.dao.AsignaturaRepository
import es.atomsusej.tareaBackend.business.IMatriculaBusisness
import es.atomsusej.tareaBackend.models.Asignatura
import es.atomsusej.tareaBackend.models.Matricula
import es.atomsusej.tareaBackend.models.MatriculaId
import es.atomsusej.tareaBackend.models.Persona
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan



@SpringBootApplication
@ComponentScan(basePackages = ["es.atomsusej.tareaBackend", "es.atomsusej.tareaBackend.config"])
open class TareaBackendApplication: CommandLineRunner{
    //Con esto traemos los metodos ya creados
    @Autowired
    val personaRepository: PersonaRepository?=null
    @Autowired
    val asignaturaRepository: AsignaturaRepository?=null
    @Autowired
    val matriculaBusisness: IMatriculaBusisness? = null

    override fun run(vararg args: String) {

        // 1. Crear y guardar PERSONAS
        val personas = listOf(
            Persona(0, "Juan", "Ramirez Alvarado", 56),
            Persona(0, "Maria", "Gonzalez Perez",   34),
            Persona(0, "Carlos","Lopez Diaz",       45),
            Persona(0, "Ana",   "Martinez Ruiz",    29),
            Persona(0, "Luis",  "Fernandez Soto",   51)
        )

        // Guardar personas y recoger los ids generados
        val personasGuardadas = personas.map { personaRepository!!.save(it) }

        // 2. Crear y guardar ASIGNATURAS
        val asignaturas = listOf(
            Asignatura("Matemáticas", "Mañana", 1),
            Asignatura("Historia",    "Tarde",   2),
            Asignatura("Física",      "Mañana",  3),
            Asignatura("Literatura",  "Tarde",   4),
            Asignatura("Biología",    "Mañana",  5)
        )

        val asignaturasGuardadas = asignaturas.map { asignaturaRepository!!.save(it) }

        // 3. Crear MATRÍCULAS usando id_asignatura + id_alumno (id_persona)
        val matriculas = listOf(
            Matricula(
                id = MatriculaId(
                    asignaturasGuardadas[0].id_asignatura,
                    personasGuardadas[0].id_persona
                ),
                nota = 7.5f
            ),
            Matricula(
                id = MatriculaId(
                    asignaturasGuardadas[1].id_asignatura,
                    personasGuardadas[1].id_persona
                ),
                nota = 8.0f
            ),
            Matricula(
                id = MatriculaId(
                    asignaturasGuardadas[2].id_asignatura,
                    personasGuardadas[2].id_persona
                ),
                nota = 6.0f
            )
        )

        // Guardar matrículas usando la capa de negocio (save)
        matriculas.forEach { matriculaBusisness!!.save(it) }

        // 4. Probar list()
        println("===== LISTA DE TODAS LAS MATRÍCULAS =====")
        matriculaBusisness!!.list().forEach { println(it) }

        // 5. Probar load(id_asignatura, id_alumno)
        val idAsigPrueba   = asignaturasGuardadas[0].id_asignatura
        val idAlumnoPrueba = personasGuardadas[0].id_persona

        println("===== PROBANDO load($idAsigPrueba, $idAlumnoPrueba) =====")
        val matriculaCargada = matriculaBusisness!!.load(idAsigPrueba, idAlumnoPrueba)
        println("Matrícula cargada: $matriculaCargada")

        // 6. Probar remove(id_asignatura, id_alumno)
        val idAsigBorrar   = asignaturasGuardadas[1].id_asignatura
        val idAlumnoBorrar = personasGuardadas[1].id_persona

        println("===== PROBANDO remove($idAsigBorrar, $idAlumnoBorrar) =====")
        matriculaBusisness!!.remove(idAsigBorrar, idAlumnoBorrar)

        // 7. Ver lista de matrículas tras borrar
        println("===== MATRÍCULAS DESPUÉS DE BORRAR UNA =====")
        matriculaBusisness!!.list().forEach { println(it) }
    }

}

fun main(args: Array<String>) {
    runApplication<TareaBackendApplication>(*args)
}