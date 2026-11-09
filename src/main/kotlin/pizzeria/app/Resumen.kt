package pizzeria.app

import pizzeria.modelo.Pedido
import pizzeria.util.dinero

fun renderizarResumen(pedido: Pedido, titulo: String): String = buildString {
    appendLine("================ $titulo ================")
    appendLine("Pedido #${pedido.idPedido} | Fecha: ${pedido.fecha}")
    appendLine("Cliente: ${pedido.cliente.nombre} | Dirección: ${pedido.cliente.direccion}")
    appendLine("Contacto: ${pedido.cliente.telefono} | ${pedido.cliente.correo}")
    pedido.productos.forEach { linea ->
        val totalLinea = linea.producto.precio * linea.cantidad
        appendLine("${linea.cantidad} x ${linea.producto.detallarProducto()} = $${dinero(totalLinea)}")
    }
    appendLine("Subtotal: $${dinero(pedido.calcularSubtotal())}")
    appendLine("Impuestos: $${dinero(pedido.calcularImpuestos())}")
    appendLine("Descuentos: $${dinero(pedido.calcularDescuento())}")
    appendLine("Total final: $${dinero(pedido.calcularTotal())}")
    appendLine("Estado: ${pedido.estado} | Repartidor: ${pedido.repartidor?.nombre ?: "Por asignar"}")
    appendLine("Notas: ${pedido.notas?.takeIf(String::isNotBlank) ?: "Sin notas"}")
    append("=".repeat(48))
}

fun construirResumenCompleto(
    resultadoStock: String,
    resumenPedido: String,
    resumenCombinado: String,
    demostracion: String,
    inspecciones: List<String>,
    pedidosReales: Int
): String = buildString {
    appendLine("RESUMEN DE PEDIDOS DE LA PIZZERÍA")
    appendLine("Resultado de inventario: $resultadoStock")
    appendLine()
    appendLine(resumenPedido)
    appendLine()
    appendLine(resumenCombinado)
    appendLine()
    appendLine(demostracion)
    inspecciones.forEach { appendLine("\n$it") }
    appendLine("\nPedidos reales registrados en esta ejecución: $pedidosReales")
}
