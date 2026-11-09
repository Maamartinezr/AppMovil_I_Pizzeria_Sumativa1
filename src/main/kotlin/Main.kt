import java.time.LocalDate
import java.util.Locale
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties

private const val TASA_IMPUESTO = 0.19
private const val DESCUENTO_MAYORISTA = 0.10
private const val DESCUENTO_PROMOCION = 0.15
private const val TELEFONO_PREDETERMINADO = "+00 (000) 000-0000"
private val CORREO_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val TELEFONO_REGEX = Regex("^\\+?([0-9]{1,3})[ .-]?\\(?([0-9]{3})\\)?[ .-]?([0-9]{3})[ .-]?([0-9]{4})$")

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable(val descripcion: String = "Promoción especial")

open class Usuario(val nombre: String, val contacto: String)

class Cliente(
    nombre: String,
    val direccion: String,
    telefono: String,
    val correo: String
) : Usuario(nombre, telefono) {
    val telefono: String get() = contacto

    operator fun component1() = nombre
    operator fun component2() = correo
    operator fun component3() = telefono

    override fun equals(other: Any?): Boolean =
        this === other || (other is Cliente && nombre.equals(other.nombre, ignoreCase = true) && correo.equals(other.correo, ignoreCase = true))

    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + correo.lowercase().hashCode()
}

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

@Promocionable("15% de descuento durante la campaña de verano")
class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>,
    val categoria: String = "pizza"
) {
    fun detallarProducto(): String = "$nombre - $${dinero(precio)} (${ingredientes.joinToString()})"

    override fun equals(other: Any?): Boolean =
        this === other || (other is Producto && nombre.equals(other.nombre, ignoreCase = true) && categoria.equals(other.categoria, ignoreCase = true))

    override fun hashCode(): Int = 31 * nombre.lowercase().hashCode() + categoria.lowercase().hashCode()
}

data class ProductoPedido(val producto: Producto, val cantidad: Int)

class Pedido(
    val idPedido: Int,
    val cliente: Cliente,
    val productos: List<ProductoPedido>,
    var estado: String = "En preparación",
    val notas: String? = null,
    var repartidor: Repartidor? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    val cantidadPizzas: Int get() = productos.sumOf { it.cantidad }
    fun calcularSubtotal(): Double = productos.sumOf { it.producto.precio * it.cantidad }
    fun calcularImpuestos(): Double = calcularSubtotal() * TASA_IMPUESTO
    fun productoEsPromocionable(producto: Producto): Boolean =
        producto::class.annotations.any { it is Promocionable }

    fun calcularDescuento(): Double {
        val subtotal = calcularSubtotal()
        val descuentoCantidad = if (cantidadPizzas > 3) subtotal * DESCUENTO_MAYORISTA else 0.0
        val dentroDePromocion = fecha in LocalDate.of(2026, 1, 1)..LocalDate.of(2026, 12, 31)
        val descuentoAnotado = if (dentroDePromocion && productos.any { productoEsPromocionable(it.producto) }) {
            subtotal * DESCUENTO_PROMOCION
        } else 0.0
        // Evita acumular descuentos por encima del subtotal.
        return (descuentoCantidad + descuentoAnotado).coerceAtMost(subtotal)
    }

    fun calcularTotal(): Double = calcularSubtotal() + calcularImpuestos() - calcularDescuento()
    fun cambiarEstado(nuevoEstado: String) { estado = nuevoEstado }

    operator fun component1() = cliente
    operator fun component2() = productos
    operator fun component3() = calcularTotal()

    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) { "Solo se pueden combinar pedidos del mismo cliente." }
        val productosCombinados = (productos + otro.productos)
            .groupBy { it.producto }
            .map { (_, lineas) -> ProductoPedido(lineas.first().producto, lineas.sumOf { it.cantidad }) }
        return Pedido(
            idPedido = minOf(idPedido, otro.idPedido),
            cliente = cliente,
            productos = productosCombinados,
            estado = "En preparación",
            notas = listOfNotNull(notas, otro.notas).takeIf { it.isNotEmpty() }?.joinToString("; "),
            fecha = maxOf(fecha, otro.fecha)
        )
    }
}

private fun dinero(valor: Double): String = String.format(Locale("es", "CL"), "%,.0f", valor)

private fun leerTexto(mensaje: String, predeterminado: String): String {
    print(mensaje)
    return readLine()?.trim()?.takeIf(String::isNotEmpty) ?: predeterminado
}

private fun leerCorreo(): String {
    while (true) {
        print("Correo electrónico: ")
        val correo = readLine()?.trim().orEmpty()
        if (correo.matches(CORREO_REGEX)) return correo
        println("Formato inválido. Ingresa un correo válido, por ejemplo nombre@dominio.com.")
    }
}

private fun formatearTelefono(entrada: String): String {
    val digitos = entrada.filter(Char::isDigit)
    if (digitos.length == 10) return "+00 (${digitos.substring(0, 3)}) ${digitos.substring(3, 6)}-${digitos.substring(6)}"
    val coincidencia = TELEFONO_REGEX.matchEntire(entrada) ?: return TELEFONO_PREDETERMINADO
    val (codigo, area, prefijo, linea) = coincidencia.destructured
    return "+$codigo ($area) $prefijo-$linea"
}

private fun leerTelefono(): String {
    while (true) {
        print("Teléfono con código de país (ejemplo: +56 912 345 678): ")
        val entrada = readLine()?.trim().orEmpty()
        val formateado = formatearTelefono(entrada)
        if (formateado != TELEFONO_PREDETERMINADO) return formateado
        println("Teléfono inválido. Reingrésalo en formato +XX (XXX) XXX-XXXX.")
    }
}

private fun leerCantidad(): Int {
    while (true) {
        print("Cantidad de pizzas (1-100): ")
        val cantidad = readLine()?.trim()?.toIntOrNull()
        if (cantidad != null && cantidad in 1..100) return cantidad
        println("Cantidad fuera de rango. Ingresa un valor entre 1 y 100.")
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
            println("Costo inválido. Ingresa un número, por ejemplo 8500.")
        } catch (error: IllegalArgumentException) {
            println("Costo no válido: ${error.message}")
        }
    }
}

private fun prepararLote(ingredientes: List<String>, cantidadPizzas: Int, inventario: MutableMap<String, Int>): Map<String, Int> {
    require(cantidadPizzas in 1..100) { "La cantidad debe estar entre 1 y 100." }
    val necesarios = ingredientes.distinct().associateWith { cantidadPizzas }
    val faltantes = necesarios.mapNotNull { (ingrediente, requerido) ->
        val falta = requerido - (inventario[ingrediente] ?: 0)
        if (falta > 0) ingrediente to falta else null
    }.toMap()
    if (faltantes.isEmpty()) necesarios.forEach { (ingrediente, cantidad) -> inventario[ingrediente] = inventario.getValue(ingrediente) - cantidad }
    return faltantes
}

private fun imprimirReflection(tipo: kotlin.reflect.KClass<*>) {
    println("\nReflection de ${tipo.simpleName}:")
    println("Propiedades: ${tipo.memberProperties.joinToString { it.name }.ifBlank { "ninguna" }}")
    println("Métodos: ${tipo.declaredMemberFunctions.joinToString { it.name }.ifBlank { "ninguno" }}")
}

private fun imprimirResumen(pedido: Pedido) {
    println("\n================ RESUMEN DEL PEDIDO ================")
    println("Pedido #${pedido.idPedido} | Fecha: ${pedido.fecha} | Cliente: ${pedido.cliente.nombre}")
    println("Contacto: ${pedido.cliente.telefono} | ${pedido.cliente.correo}")
    pedido.productos.forEach { println("${it.cantidad} x ${it.producto.detallarProducto()} = $${dinero(it.producto.precio * it.cantidad)}") }
    println("Subtotal: $${dinero(pedido.calcularSubtotal())} | Impuestos: $${dinero(pedido.calcularImpuestos())}")
    println("Descuentos: $${dinero(pedido.calcularDescuento())} | Total: $${dinero(pedido.calcularTotal())}")
    println("Estado: ${pedido.estado} | Repartidor: ${pedido.repartidor?.nombre ?: "Por asignar"}")
    println("Notas: ${pedido.notas?.takeIf(String::isNotBlank) ?: "Sin notas"}")
    println("=====================================================")
}

fun main() {
    println("=== Gestión de pedidos de la pizzería ===")
    val nombre = leerTexto("Nombre del cliente: ", "Cliente sin nombre")
    val direccion = leerTexto("Dirección de entrega: ", "Dirección no registrada")
    val cliente = Cliente(nombre, direccion, leerTelefono(), leerCorreo())
    val producto = Producto(
        nombre = leerTexto("Nombre de la pizza: ", "Pizza especial"),
        precio = leerPrecio(),
        ingredientes = listOf("masa", "salsa", "queso")
    )
    val cantidad = leerCantidad()
    print("Notas del pedido (opcional): ")
    val notas = readLine()?.trim()?.takeIf(String::isNotEmpty)

    val inventario = mutableMapOf("masa" to 100, "salsa" to 100, "queso" to 100)
    val pedidos = mutableListOf<Pedido>()
    val pedido = Pedido(1, cliente, listOf(ProductoPedido(producto, cantidad)), notas = notas)
    val faltantes = prepararLote(producto.ingredientes, cantidad, inventario)
    if (faltantes.isEmpty()) {
        pedido.repartidor = listOf(
            Repartidor("Camila", "+56 (911) 111-1111", 2.4),
            Repartidor("Diego", "+56 (922) 222-2222", 1.2)
        ).sortedBy { it.distanciaKm }.find { it.disponible }
        println("Pedido confirmado. Stock actualizado.")
        pedido.cliente.correo.let { println("Notificación enviada a $it") }
    } else {
        pedido.cambiarEstado("Pendiente por falta de ingredientes")
        println("Stock insuficiente: ${faltantes.entries.joinToString { "${it.key}: faltan ${it.value}" }}. Reponer ingredientes y volver a procesar el pedido.")
    }
    pedidos.add(pedido)

    // Demostración de igualdad, registros sin duplicados y combinación de pedidos.
    val clientesRegistrados = mutableSetOf(cliente)
    clientesRegistrados.add(Cliente(cliente.nombre, cliente.direccion, cliente.telefono, cliente.correo))
    val productosRegistrados = mutableSetOf(producto)
    productosRegistrados.add(Producto(producto.nombre, producto.precio, producto.ingredientes, producto.categoria))
    val segundoPedido = Pedido(2, cliente, listOf(ProductoPedido(producto, 1)), notas = "Pedido combinado")
    val combinado = pedido + segundoPedido

    val (nombreCliente, correoCliente, telefonoCliente) = cliente
    val (clientePedido, productosPedido, totalPedido) = combinado
    println("\n================ DEMOSTRACIÓN PARTE 3 ================")
    println("Cliente desestructurado: $nombreCliente | $correoCliente | $telefonoCliente")
    println("Pedido desestructurado: ${clientePedido.nombre} | ${productosPedido.size} línea(s) | Total combinado: $${dinero(totalPedido)}")
    println("Pedidos combinados: #${pedido.idPedido} + #${segundoPedido.idPedido} = #${combinado.idPedido}")
    println("Producto promocionable: ${producto::class.annotations.filterIsInstance<Promocionable>().joinToString { it.descripcion }.ifBlank { "No" }}")
    println("Clientes únicos: ${clientesRegistrados.size} | Productos únicos: ${productosRegistrados.size}")
    println("Igualdad: cliente duplicado=${cliente == Cliente(cliente.nombre, cliente.direccion, cliente.telefono, cliente.correo)}, producto duplicado=${producto == Producto(producto.nombre, 0.0, emptyList(), producto.categoria)}")
    println("Rango promocional: ${pedido.fecha in LocalDate.of(2026, 1, 1)..LocalDate.of(2026, 12, 31)}")
    println("=======================================================")

    imprimirResumen(pedido)
    imprimirResumen(combinado)
    imprimirReflection(Pedido::class)
    imprimirReflection(Producto::class)
    imprimirReflection(Cliente::class)
    println("\nPedidos registrados en esta ejecución: ${pedidos.size}")
}
