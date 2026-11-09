package pizzeria.util

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties

fun inspeccionarClase(tipo: KClass<*>): String = buildString {
    appendLine("Reflection de ${tipo.simpleName}")
    appendLine("Propiedades: ${tipo.memberProperties.joinToString { it.name }.ifBlank { "ninguna" }}")
    append("Métodos: ${tipo.declaredMemberFunctions.joinToString { it.name }.ifBlank { "ninguno" }}")
}
