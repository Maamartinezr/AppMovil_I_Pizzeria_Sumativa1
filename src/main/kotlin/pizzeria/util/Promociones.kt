package pizzeria.util

import java.time.LocalDate
import pizzeria.anotaciones.Promocionable
import pizzeria.modelo.ProductoPedido
import kotlin.reflect.full.findAnnotation

const val TASA_IMPUESTO = 0.19
const val DESCUENTO_MAYORISTA = 0.10
const val DESCUENTO_PROMOCION = 0.15

val RANGO_PROMOCIONAL: ClosedRange<LocalDate> =
    LocalDate.of(2026, 1, 1)..LocalDate.of(2026, 12, 31)

fun esPromocionable(producto: pizzeria.modelo.Producto): Boolean =
    producto::class.findAnnotation<Promocionable>() != null

fun calcularDescuentoPromocional(
    productos: List<ProductoPedido>,
    fecha: LocalDate
): Double {
    if (fecha !in RANGO_PROMOCIONAL) return 0.0
    val subtotalElegible = productos
        .filter { esPromocionable(it.producto) }
        .sumOf { it.producto.precio * it.cantidad }
    return subtotalElegible * DESCUENTO_PROMOCION
}
