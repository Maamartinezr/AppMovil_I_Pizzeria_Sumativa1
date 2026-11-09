package pizzeria.modelo

import java.time.LocalDate
import pizzeria.util.DESCUENTO_MAYORISTA
import pizzeria.util.TASA_IMPUESTO
import pizzeria.util.calcularDescuentoPromocional

class Pedido(
    val idPedido: Int,
    val cliente: Cliente,
    val productos: List<ProductoPedido>,
    var estado: String = "En preparación",
    val notas: String? = null,
    var repartidor: Repartidor? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    init {
        require(idPedido > 0) { "El identificador del pedido debe ser positivo." }
        require(productos.isNotEmpty()) { "El pedido debe contener al menos un producto." }
    }

    val cantidadPizzas: Int
        get() = productos.sumOf { it.cantidad }

    fun calcularSubtotal(): Double = productos.sumOf { it.producto.precio * it.cantidad }

    fun calcularDescuento(): Double {
        val subtotal = calcularSubtotal()
        val descuentoPorCantidad =
            if (cantidadPizzas > 3) subtotal * DESCUENTO_MAYORISTA else 0.0
        val descuentoPromocional = calcularDescuentoPromocional(productos, fecha)
        return (descuentoPorCantidad + descuentoPromocional).coerceAtMost(subtotal)
    }

    fun calcularImpuestos(): Double = (calcularSubtotal() - calcularDescuento()) * TASA_IMPUESTO

    fun calcularTotal(): Double = calcularSubtotal() - calcularDescuento() + calcularImpuestos()

    fun cambiarEstado(nuevoEstado: String) {
        estado = nuevoEstado
    }

    operator fun component1(): Cliente = cliente
    operator fun component2(): List<ProductoPedido> = productos
    operator fun component3(): Double = calcularTotal()

    /** Combina dos pedidos del mismo cliente sin modificar los pedidos originales. */
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se pueden combinar pedidos del mismo cliente."
        }
        val lineasCombinadas = (productos + otro.productos)
            .groupBy { it.producto }
            .map { (_, lineas) ->
                ProductoPedido(lineas.first().producto, lineas.sumOf { it.cantidad })
            }
        return Pedido(
            idPedido = maxOf(idPedido, otro.idPedido) + 1,
            cliente = cliente,
            productos = lineasCombinadas,
            estado = "En preparación",
            notas = listOfNotNull(notas, otro.notas)
                .takeIf { it.isNotEmpty() }
                ?.joinToString("; "),
            fecha = maxOf(fecha, otro.fecha)
        )
    }
}
