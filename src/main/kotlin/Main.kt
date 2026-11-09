import java.util.Locale

private const val TASA_IMPUESTO = 0.19
private const val DESCUENTO_MAYORISTA = 0.10
private const val CORREO_PREDETERMINADO = "correo@invalido.com"
private const val TELEFONO_PREDETERMINADO = "(000) 000-0000"

open class Usuario(
    val nombre: String,
    val contacto: String
)

class Cliente(
    nombre: String,
    val direccion: String,
    val telefono: String,
    val correo: String
) : Usuario(nombre, telefono)

class Repartidor(
    nombre: String,
    contacto: String,
    val distanciaKm: Double,
    var disponible: Boolean = true
) : Usuario(nombre, contacto) {
    fun entregarPedido(pedido: Pedido): String {
        pedido.cambiarEstado("En camino")
        disponible = false
        return "$nombre lleva el pedido #${pedido.idPedido} en camino."
    }
}

class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>
) {
    fun detallarProducto(): String = "$nombre - $${dinero(precio)} (ingredientes: ${ingredientes.joinToString()})"
}

data class ProductoPedido(val producto: Producto, val cantidad: Int)

class Pedido(
    val idPedido: Int,
    val cliente: Cliente,
    val productos: List<ProductoPedido>,
    var estado: String = "En preparación",
    val notas: String? = null,
    var repartidor: Repartidor? = null
) {
    val cantidadPizzas: Int
        get() = productos.sumOf { it.cantidad }

    fun calcularSubtotal(): Double = productos.sumOf { it.producto.precio * it.cantidad }
    fun calcularImpuestos(): Double = calcularSubtotal() * TASA_IMPUESTO
    fun calcularDescuento(): Double = if (cantidadPizzas > 3) calcularSubtotal() * DESCUENTO_MAYORISTA else 0.0
    fun calcularTotal(): Double = calcularSubtotal() + calcularImpuestos() - calcularDescuento()
    fun cambiarEstado(nuevoEstado: String) { estado = nuevoEstado }
}

private fun dinero(valor: Double): String = String.format(Locale("es", "CL"), "%,.0f", valor)

private fun leerTexto(mensaje: String, predeterminado: String): String {
    print(mensaje)
    return readLine()?.trim()?.takeIf(String::isNotEmpty) ?: predeterminado
}

private fun leerTelefono(): String {
    print("Teléfono (ejemplo: +56912345678): ")
    val entrada = readLine()?.trim().orEmpty()
    val normalizado = entrada.replace(Regex("[\\s()-]"), "")
    return if (normalizado.matches(Regex("\\+?[0-9]{7,15}"))) entrada else {
        println("Teléfono inválido; se utilizará $TELEFONO_PREDETERMINADO.")
        TELEFONO_PREDETERMINADO
    }
}

private fun leerCorreo(): String {
    print("Correo electrónico: ")
    val entrada = readLine()?.trim().orEmpty()
    return if (entrada.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) entrada else {
        println("Correo inválido; se utilizará $CORREO_PREDETERMINADO.")
        CORREO_PREDETERMINADO
    }
}

private fun leerEntero(mensaje: String, minimo: Int): Int {
    while (true) {
        print(mensaje)
        val entrada = readLine() ?: return minimo
        try {
            val numero = entrada.trim().toInt()
            require(numero >= minimo) { "El valor debe ser mayor o igual a $minimo." }
            return numero
        } catch (error: NumberFormatException) {
            println("Entrada inválida: ingresa un número entero.")
        } catch (error: IllegalArgumentException) {
            println("Cantidad no válida: ${error.message}")
        }
    }
}

private fun leerPrecio(): Double {
    while (true) {
        print("Precio por pizza: $")
        val entrada = readLine() ?: return 0.0
        try {
            val precio = entrada.trim().replace(',', '.').toDouble()
            require(precio >= 0.0) { "El precio no puede ser negativo." }
            return precio
        } catch (error: NumberFormatException) {
            println("Costo inválido: ingresa un número, por ejemplo 8500.")
        } catch (error: IllegalArgumentException) {
            println("Costo no válido: ${error.message}")
        }
    }
}

private fun prepararLote(
    ingredientes: List<String>,
    cantidadPizzas: Int,
    inventario: MutableMap<String, Int>
): Map<String, Int> {
    require(cantidadPizzas > 0) { "La cantidad debe ser positiva." }
    val necesarios = ingredientes.distinct().associateWith { cantidadPizzas }
    val faltantes = necesarios.filter { (ingrediente, cantidad) ->
        (inventario[ingrediente] ?: 0) < cantidad
    }
    if (faltantes.isEmpty()) {
        necesarios.forEach { (ingrediente, cantidad) ->
            inventario[ingrediente] = (inventario[ingrediente] ?: 0) - cantidad
        }
    }
    return faltantes
}

private fun resumenStock(faltantes: Map<String, Int>): String {
    val estado = if (faltantes.isEmpty()) "DISPONIBLE" else "INSUFICIENTE"
    return when (estado) {
        "DISPONIBLE" -> "Stock suficiente: pedido aceptado y el inventario fue actualizado."
        "INSUFICIENTE" -> buildString {
            appendLine("No se puede aceptar el pedido. Ingredientes insuficientes:")
            faltantes.forEach { (ingrediente, cantidad) ->
                appendLine("- $ingrediente: faltan $cantidad unidad(es).")
            }
            append("Reabastecimiento: registrar la compra, reponer las cantidades faltantes y volver a verificar el inventario.")
        }
        else -> "No fue posible verificar el inventario."
    }
}

private fun imprimirReporte(pedidos: Array<Pedido>) {
    println("\n========== REPORTE DE PEDIDOS DEL DÍA ==========")
    for (pedido in pedidos) {
        val pizzas = pedido.productos.joinToString { "${it.cantidad} x ${it.producto.nombre}" }
        println("Pedido #${pedido.idPedido} | Cliente: ${pedido.cliente.nombre} | Pizzas: $pizzas | Total: $${dinero(pedido.calcularTotal())} | Estado: ${pedido.estado}")
    }
    val completados = pedidos.filter { it.estado == "Entregado" }
    val pendientes = pedidos.filter { it.estado != "Entregado" }
    println("Completados: ${completados.size} | Pendientes: ${pendientes.size}")
    println("================================================")
}

private fun imprimirResumen(pedido: Pedido) {
    println("\n=============== RESUMEN DEL PEDIDO ===============")
    println("Pedido #${pedido.idPedido}")
    println("Cliente: ${pedido.cliente.nombre}")
    println("Dirección: ${pedido.cliente.direccion}")
    println("Teléfono: ${pedido.cliente.telefono} | Correo: ${pedido.cliente.correo}")
    println("Productos:")
    pedido.productos.forEach { linea ->
        println("  ${linea.producto.detallarProducto()} | Cantidad: ${linea.cantidad} | Total: $${dinero(linea.producto.precio * linea.cantidad)}")
    }
    println("Subtotal: $${dinero(pedido.calcularSubtotal())}")
    println("Impuestos (${(TASA_IMPUESTO * 100).toInt()}%): $${dinero(pedido.calcularImpuestos())}")
    println("Descuento${if (pedido.cantidadPizzas > 3) " (10%)" else " (no aplica)"}: $${dinero(pedido.calcularDescuento())}")
    println("Total final: $${dinero(pedido.calcularTotal())}")
    println("Estado: ${pedido.estado}")
    println("Repartidor: ${pedido.repartidor?.nombre ?: "Por asignar"}")
    println("Notas: ${pedido.notas?.takeIf { it.isNotBlank() } ?: "Sin notas"}")
    println("===================================================")
}

fun main() {
    println("=== Gestión de pedidos - Pizzería ===")
    val nombre = leerTexto("Nombre del cliente: ", "Cliente sin nombre")
    val direccion = leerTexto("Dirección de entrega: ", "Dirección no registrada")
    val cliente = Cliente(nombre, direccion, leerTelefono(), leerCorreo())

    val pizza = Producto(
        nombre = leerTexto("Nombre de la pizza: ", "Pizza especial"),
        precio = leerPrecio(),
        ingredientes = listOf("masa", "salsa", "queso")
    )
    val cantidad = leerEntero("Cantidad de pizzas: ", 1)
    print("Notas del pedido (opcional): ")
    val notas = readLine()?.trim()?.takeIf { it.isNotEmpty() }

    val inventario = mutableMapOf("masa" to 20, "salsa" to 20, "queso" to 20)
    try {
        inventario.values.forEach { require(it >= 0) { "El inventario no puede ser negativo." } }
    } catch (error: IllegalArgumentException) {
        println("Error de inventario: ${error.message}")
        return
    }

    val pedido = Pedido(1, cliente, listOf(ProductoPedido(pizza, cantidad)), notas = notas)
    val faltantes = try {
        prepararLote(pizza.ingredientes, pedido.cantidadPizzas, inventario)
    } catch (error: IllegalArgumentException) {
        println("No se pudo procesar el pedido: ${error.message}")
        return
    }
    println(resumenStock(faltantes))

    if (faltantes.isEmpty()) {
        val repartidoresDisponibles = listOf(
            Repartidor("Camila", "+56911111111", 2.4),
            Repartidor("Diego", "+56922222222", 1.2),
            Repartidor("Sofía", "+56933333333", 3.0)
        )
        // Se ordena por distancia y find selecciona al repartidor disponible más cercano.
        pedido.repartidor = repartidoresDisponibles.sortedBy { it.distanciaKm }.find { it.disponible }
        pedido.repartidor?.let { println("Notificación enviada a ${pedido.cliente.nombre} (${pedido.cliente.correo}).") }
    } else {
        pedido.cambiarEstado("Pendiente por falta de ingredientes")
    }

    imprimirResumen(pedido)
    // El arreglo conserva los pedidos registrados durante esta ejecución para elaborar el reporte del día.
    val pedidosDelDia: Array<Pedido> = arrayOf(pedido)
    imprimirReporte(pedidosDelDia)
}
