import java.time.LocalDate
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

private const val TASA_IMPUESTO = 0.19
private const val DESCUENTO_MAYORISTA = 0.10
private const val DESCUENTO_PROMOCION = 0.15
private const val TELEFONO_PREDETERMINADO = "+00 (000) 000-0000"
private const val CORREO_PREDETERMINADO = "correo@invalido.com"
private val RANGO_PROMOCIONAL = LocalDate.of(2026, 1, 1)..LocalDate.of(2026, 12, 31)
private val CORREO_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val TELEFONO_REGEX = Regex("^\\+?([0-9]{1,3})[ .-]?\\(?([0-9]{3})\\)?[ .-]?([0-9]{3})[ .-]?([0-9]{4})$")

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable(val descripcion: String = "Promoción especial")

open class Usuario(
    val nombre: String,
    val contacto: String
)

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
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacío." }
        require(precio >= 0.0) { "El precio no puede ser negativo." }
        require(categoria.isNotBlank()) { "La categoría no puede estar vacía." }
    }

    fun detallarProducto(): String =
        "$nombre - $${dinero(precio)} (${ingredientes.joinToString()})"

    override fun equals(other: Any?): Boolean =
        this === other || (other is Producto &&
            nombre.equals(other.nombre, ignoreCase = true) &&
            categoria.equals(other.categoria, ignoreCase = true))

    override fun hashCode(): Int =
        31 * nombre.lowercase(Locale.ROOT).hashCode() + categoria.lowercase(Locale.ROOT).hashCode()
}

data class ProductoPedido(
    val producto: Producto,
    val cantidad: Int
) {
    init {
        require(cantidad in 1..100) { "La cantidad debe estar entre 1 y 100." }
    }
}

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

    /** Impuestos de ejemplo; se calculan sobre el subtotal después de descuentos. */
    fun calcularImpuestos(): Double = (calcularSubtotal() - calcularDescuento()) * TASA_IMPUESTO

    fun productoEsPromocionable(producto: Producto): Boolean =
        producto::class.findAnnotation<Promocionable>() != null

    fun calcularDescuento(): Double {
        val subtotal = calcularSubtotal()
        val descuentoPorCantidad =
            if (cantidadPizzas > 3) subtotal * DESCUENTO_MAYORISTA else 0.0
        val dentroDelPeriodo = fecha in RANGO_PROMOCIONAL
        val subtotalPromocionable = productos
            .filter { productoEsPromocionable(it.producto) }
            .sumOf { it.producto.precio * it.cantidad }
        val descuentoPromocional =
            if (dentroDelPeriodo) subtotalPromocionable * DESCUENTO_PROMOCION else 0.0

        // Para este ejercicio los descuentos son acumulables, con tope del subtotal.
        return (descuentoPorCantidad + descuentoPromocional).coerceAtMost(subtotal)
    }

    fun calcularTotal(): Double = calcularSubtotal() + calcularImpuestos() - calcularDescuento()

    fun cambiarEstado(nuevoEstado: String) {
        estado = nuevoEstado
    }

    operator fun component1(): Cliente = cliente
    operator fun component2(): List<ProductoPedido> = productos
    operator fun component3(): Double = calcularTotal()

    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se pueden combinar pedidos del mismo cliente."
        }
        val productosCombinados = (productos + otro.productos)
            .groupBy { it.producto }
            .map { (_, lineas) ->
                ProductoPedido(
                    producto = lineas.first().producto,
                    cantidad = lineas.sumOf { it.cantidad }
                )
            }
        return Pedido(
            idPedido = maxOf(idPedido, otro.idPedido) + 1,
            cliente = cliente,
            productos = productosCombinados,
            estado = "En preparación",
            notas = listOfNotNull(notas, otro.notas)
                .takeIf { it.isNotEmpty() }
                ?.joinToString("; "),
            fecha = maxOf(fecha, otro.fecha)
        )
    }
}

private fun dinero(valor: Double): String =
    String.format(Locale("es", "CL"), "%,.0f", valor)

private fun leerTexto(mensaje: String, predeterminado: String): String {
    print(mensaje)
    return readLine()?.trim()?.takeIf(String::isNotEmpty) ?: predeterminado
}

private fun leerCorreo(): String {
    while (true) {
        print("Correo electrónico: ")
        val correo = readLine()?.trim() ?: return CORREO_PREDETERMINADO
        if (correo.matches(CORREO_REGEX)) return correo
        println("Formato inválido. Ingresa un correo válido, por ejemplo nombre@dominio.com.")
    }
}

/** Convierte números de formato internacional a +XX (XXX) XXX-XXXX. */
private fun formatearTelefono(entrada: String): String {
    val coincidencia = TELEFONO_REGEX.matchEntire(entrada.trim()) ?: return TELEFONO_PREDETERMINADO
    val (codigoPais, area, prefijo, linea) = coincidencia.destructured
    return "+$codigoPais ($area) $prefijo-$linea"
}

private fun leerTelefono(): String {
    while (true) {
        print("Teléfono (ejemplo: +56 (912) 345-6789): ")
        val entrada = readLine() ?: return TELEFONO_PREDETERMINADO
        val telefono = formatearTelefono(entrada)
        if (telefono != TELEFONO_PREDETERMINADO) return telefono
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
        } catch (_: NumberFormatException) {
            println("Costo inválido. Ingresa un número, por ejemplo 8500.")
        } catch (error: IllegalArgumentException) {
            println("Costo no válido: ${error.message}")
        }
    }
}

/** Descuenta ingredientes únicamente si existe stock suficiente para todos. */
private fun prepararLote(
    ingredientes: List<String>,
    cantidadPizzas: Int,
    inventario: MutableMap<String, Int>
): Map<String, Int> {
    require(cantidadPizzas in 1..100) { "La cantidad debe estar entre 1 y 100." }
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

private fun mensajeDisponibilidad(faltantes: Map<String, Int>): String = when {
    faltantes.isEmpty() -> "Stock suficiente: pedido aceptado e inventario actualizado."
    else -> buildString {
        appendLine("No se puede aceptar el pedido; faltan los siguientes ingredientes:")
        faltantes.forEach { (ingrediente, cantidad) ->
            appendLine("- $ingrediente: faltan $cantidad unidad(es).")
        }
        append("Registrar la compra, reponer el inventario y verificarlo nuevamente.")
    }
}

private fun imprimirReflection(tipo: KClass<*>) {
    println("\nReflection de ${tipo.simpleName}:")
    println("Propiedades: ${tipo.memberProperties.joinToString { it.name }.ifBlank { "ninguna" }}")
    println("Métodos: ${tipo.declaredMemberFunctions.joinToString { it.name }.ifBlank { "ninguno" }}")
}

private fun imprimirResumen(pedido: Pedido, titulo: String = "RESUMEN DEL PEDIDO") {
    println("\n================ $titulo ================")
    println("Pedido #${pedido.idPedido} | Fecha: ${pedido.fecha}")
    println("Cliente: ${pedido.cliente.nombre} | Dirección: ${pedido.cliente.direccion}")
    println("Contacto: ${pedido.cliente.telefono} | ${pedido.cliente.correo}")
    pedido.productos.forEach { linea ->
        val totalLinea = linea.producto.precio * linea.cantidad
        println("${linea.cantidad} x ${linea.producto.detallarProducto()} = $${dinero(totalLinea)}")
    }
    println("Subtotal: $${dinero(pedido.calcularSubtotal())}")
    println("Impuestos (${(TASA_IMPUESTO * 100).toInt()}%): $${dinero(pedido.calcularImpuestos())}")
    println("Descuentos: $${dinero(pedido.calcularDescuento())}")
    println("Total final: $${dinero(pedido.calcularTotal())}")
    println("Estado: ${pedido.estado} | Repartidor: ${pedido.repartidor?.nombre ?: "Por asignar"}")
    println("Notas: ${pedido.notas?.takeIf(String::isNotBlank) ?: "Sin notas"}")
    println("=".repeat(48))
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
    val pedido = Pedido(
        idPedido = 1,
        cliente = cliente,
        productos = listOf(ProductoPedido(producto, cantidad)),
        notas = notas
    )
    val faltantes = prepararLote(producto.ingredientes, cantidad, inventario)
    println(mensajeDisponibilidad(faltantes))

    if (faltantes.isEmpty()) {
        val repartidoresDisponibles = listOf(
            Repartidor("Camila", "+56 (911) 111-1111", 2.4),
            Repartidor("Diego", "+56 (922) 222-2222", 1.2)
        )
        pedido.repartidor = repartidoresDisponibles
            .sortedBy { it.distanciaKm }
            .find { it.disponible }
        pedido.cliente.correo.let { println("Notificación de confirmación enviada a $it.") }
    } else {
        pedido.cambiarEstado("Pendiente por falta de ingredientes")
    }

    // La colección se mantiene en memoria solo durante esta demostración.
    val pedidosDelDia = mutableListOf(pedido)

    // Demostración de igualdad, deduplicación, sobrecarga y desestructuración.
    val clientesRegistrados = mutableSetOf(cliente)
    clientesRegistrados.add(Cliente(cliente.nombre, cliente.direccion, cliente.telefono, cliente.correo))
    val productosRegistrados = mutableSetOf(producto)
    productosRegistrados.add(Producto(producto.nombre, producto.precio, producto.ingredientes, producto.categoria))

    val segundoPedido = Pedido(
        idPedido = 2,
        cliente = cliente,
        productos = listOf(ProductoPedido(producto, 1)),
        notas = "Pedido de demostración"
    )
    val combinado = pedido + segundoPedido
    val (nombreCliente, correoCliente, telefonoCliente) = cliente
    val (clientePedido, productosPedido, totalPedido) = combinado

    println("\n================ DEMOSTRACIÓN PARTE 3 ================")
    println("Cliente desestructurado: $nombreCliente | $correoCliente | $telefonoCliente")
    println("Pedido desestructurado: ${clientePedido.nombre} | ${productosPedido.size} línea(s) | Total combinado: $${dinero(totalPedido)}")
    println("Pedidos combinados: #${pedido.idPedido} + #${segundoPedido.idPedido} = #${combinado.idPedido}")
    val promocion = producto::class.findAnnotation<Promocionable>()?.descripcion ?: "No promocionable"
    println("Producto promocionable: $promocion")
    println("Clientes únicos: ${clientesRegistrados.size} | Productos únicos: ${productosRegistrados.size}")
    println("Igualdad: cliente duplicado=${cliente == clientesRegistrados.last()}, producto duplicado=${producto == productosRegistrados.last()}")
    println("Fecha dentro de promoción: ${pedido.fecha in RANGO_PROMOCIONAL}")
    println("=======================================================")

    imprimirResumen(pedido)
    imprimirResumen(combinado, "RESUMEN DEL PEDIDO COMBINADO")
    imprimirReflection(Pedido::class)
    imprimirReflection(Producto::class)
    imprimirReflection(Cliente::class)
    println("\nPedidos de la ejecución: ${pedidosDelDia.size}")
}
