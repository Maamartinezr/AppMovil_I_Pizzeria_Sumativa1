package pizzeria.modelo

import java.util.Locale
import pizzeria.anotaciones.Promocionable
import pizzeria.util.dinero

@Promocionable("15% de descuento durante la campaña promocional")
class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>,
    val categoria: String = "pizza"
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacío." }
        require(precio.isFinite() && precio >= 0.0) { "El precio debe ser un número válido y no negativo." }
        require(categoria.isNotBlank()) { "La categoría no puede estar vacía." }
    }

    fun detallarProducto(): String = "$nombre - $${dinero(precio)} (${ingredientes.joinToString()})"

    override fun equals(other: Any?): Boolean =
        this === other || (other is Producto &&
            nombre.equals(other.nombre, ignoreCase = true) &&
            categoria.equals(other.categoria, ignoreCase = true))

    override fun hashCode(): Int =
        31 * nombre.lowercase(Locale.ROOT).hashCode() + categoria.lowercase(Locale.ROOT).hashCode()
}
