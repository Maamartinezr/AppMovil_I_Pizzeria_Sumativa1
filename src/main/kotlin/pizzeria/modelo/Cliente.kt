package pizzeria.modelo

import java.util.Locale

/** Cliente identificado de forma única por nombre y correo electrónico. */
class Cliente(
    nombre: String,
    val direccion: String,
    telefono: String,
    val correo: String
) : Usuario(nombre, telefono) {
    val telefono: String
        get() = contacto

    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    override fun equals(other: Any?): Boolean =
        this === other || (other is Cliente &&
            nombre.equals(other.nombre, ignoreCase = true) &&
            correo.equals(other.correo, ignoreCase = true))

    override fun hashCode(): Int =
        31 * nombre.lowercase(Locale.ROOT).hashCode() + correo.lowercase(Locale.ROOT).hashCode()
}
