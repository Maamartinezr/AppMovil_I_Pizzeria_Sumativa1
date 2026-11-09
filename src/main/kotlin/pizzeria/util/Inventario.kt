package pizzeria.util

/** Revisa todo el lote antes de modificar el stock para no dejar descuentos parciales. */
fun prepararLote(
    ingredientes: List<String>,
    cantidadPizzas: Int,
    inventario: MutableMap<String, Int>
): Map<String, Int> {
    require(cantidadPizzas in 1..100) { "La cantidad debe estar entre 1 y 100." }
    require(inventario.values.all { it >= 0 }) { "El inventario no puede contener valores negativos." }

    val necesarios = ingredientes.distinct().associateWith { cantidadPizzas }
    val faltantes = necesarios.mapNotNull { (ingrediente, requerido) ->
        val falta = requerido - (inventario[ingrediente] ?: 0)
        if (falta > 0) ingrediente to falta else null
    }.toMap()

    if (faltantes.isEmpty()) {
        necesarios.forEach { (ingrediente, cantidad) ->
            inventario[ingrediente] = inventario.getValue(ingrediente) - cantidad
        }
    }
    return faltantes
}

fun mensajeDisponibilidad(faltantes: Map<String, Int>): String = when {
    faltantes.isEmpty() -> "Stock suficiente: pedido aceptado e inventario actualizado."
    else -> buildString {
        appendLine("No se puede aceptar el pedido; faltan los siguientes ingredientes:")
        faltantes.forEach { (ingrediente, cantidad) ->
            appendLine("- $ingrediente: faltan $cantidad unidad(es).")
        }
        append("Registrar la compra, reponer el inventario y verificarlo nuevamente.")
    }
}
