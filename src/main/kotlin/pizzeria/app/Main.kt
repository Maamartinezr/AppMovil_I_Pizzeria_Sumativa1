package pizzeria.app

import java.time.LocalDate
import pizzeria.anotaciones.Promocionable
import pizzeria.modelo.Cliente
import pizzeria.modelo.Pedido
import pizzeria.modelo.Producto
import pizzeria.modelo.ProductoPedido
import pizzeria.modelo.Repartidor
import pizzeria.util.RANGO_PROMOCIONAL
import pizzeria.util.dinero
import pizzeria.util.inspeccionarClase
import pizzeria.util.leerCantidad
import pizzeria.util.leerCorreo
import pizzeria.util.leerPrecio
import pizzeria.util.leerTelefono
import pizzeria.util.leerTexto
import pizzeria.util.mensajeDisponibilidad
import pizzeria.util.prepararLote
import pizzeria.util.registrarCliente
import pizzeria.util.registrarProducto

fun main() {
    println("=== Gestión de pedidos de la pizzería ===")
    val clientesRegistrados = mutableSetOf<Cliente>()
    val productosRegistrados = mutableSetOf<Producto>()

    val cliente = Cliente(
        nombre = leerTexto("Nombre del cliente: ", "Cliente sin nombre"),
        direccion = leerTexto("Dirección de entrega: ", "Dirección no registrada"),
        telefono = leerTelefono(),
        correo = leerCorreo()
    )
    check(registrarCliente(cliente, clientesRegistrados))

    val producto = Producto(
        nombre = leerTexto("Nombre de la pizza: ", "Pizza especial"),
        precio = leerPrecio(),
        ingredientes = listOf("masa", "salsa", "queso")
    )
    check(registrarProducto(producto, productosRegistrados))

    val cantidad = leerCantidad()
    print("Notas del pedido (opcional): ")
    val notas = readLine()?.trim()?.takeIf(String::isNotEmpty)

    val inventario = mutableMapOf("masa" to 100, "salsa" to 100, "queso" to 100)
    val pedido = Pedido(1, cliente, listOf(ProductoPedido(producto, cantidad)), notas = notas)
    val faltantes = prepararLote(producto.ingredientes, cantidad, inventario)
    val resultadoStock = mensajeDisponibilidad(faltantes)
    println(resultadoStock)

    if (faltantes.isEmpty()) {
        val repartidores = listOf(
            Repartidor("Camila", "+56 (911) 111-1111", 2.4),
            Repartidor("Diego", "+56 (922) 222-2222", 1.2)
        )
        pedido.repartidor = repartidores.sortedBy { it.distanciaKm }.find { it.disponible }
        pedido.cliente.correo.let { println("Notificación de confirmación enviada a $it.") }
    } else {
        pedido.cambiarEstado("Pendiente por falta de ingredientes")
    }

    val pedidosDelDia = mutableListOf(pedido)

    // La misma función de registro usada para altas reales rechaza entidades duplicadas.
    val clienteDuplicado = Cliente(cliente.nombre, cliente.direccion, cliente.telefono, cliente.correo)
    val productoDuplicado = Producto(producto.nombre, producto.precio + 100.0, producto.ingredientes, producto.categoria)
    val intentoClienteDuplicadoRechazado = !registrarCliente(clienteDuplicado, clientesRegistrados)
    val intentoProductoDuplicadoRechazado = !registrarProducto(productoDuplicado, productosRegistrados)

    val segundoPedido = Pedido(
        idPedido = 2,
        cliente = cliente,
        productos = listOf(ProductoPedido(producto, 1)),
        notas = "Pedido de demostración",
        fecha = LocalDate.now()
    )
    val pedidoCombinado = pedido + segundoPedido
    val (nombreCliente, correoCliente, telefonoCliente) = cliente
    val (clientePedido, productosPedido, totalPedido) = pedidoCombinado
    val promocion = producto::class.annotations
        .filterIsInstance<Promocionable>()
        .firstOrNull()
        ?.descripcion ?: "No promocionable"

    val demostracion = buildString {
        appendLine("================ DEMOSTRACIÓN PARTE 3 ================")
        appendLine("Cliente desestructurado: $nombreCliente | $correoCliente | $telefonoCliente")
        appendLine("Pedido desestructurado: ${clientePedido.nombre} | ${productosPedido.size} línea(s) | Total combinado: $${dinero(totalPedido)}")
        appendLine("Pedidos combinados: #${pedido.idPedido} + #${segundoPedido.idPedido} = #${pedidoCombinado.idPedido}")
        appendLine("Producto promocionable: $promocion")
        appendLine("Intento duplicado de cliente rechazado: $intentoClienteDuplicadoRechazado")
        appendLine("Intento duplicado de producto rechazado: $intentoProductoDuplicadoRechazado")
        appendLine("Clientes registrados: ${clientesRegistrados.size} | Productos registrados: ${productosRegistrados.size}")
        appendLine("Igualdad estructural (==): cliente=${cliente == clienteDuplicado}, producto=${producto == productoDuplicado}")
        appendLine("Igualdad referencial (===): cliente=${cliente === clienteDuplicado}, producto=${producto === productoDuplicado}")
        appendLine("Fecha dentro del rango promocional: ${pedido.fecha in RANGO_PROMOCIONAL}")
        appendLine("=======================================================")
    }

    val resumen = construirResumenCompleto(
        resultadoStock = resultadoStock,
        resumenPedido = renderizarResumen(pedido, "RESUMEN DEL PEDIDO"),
        resumenCombinado = renderizarResumen(pedidoCombinado, "RESUMEN DEL PEDIDO COMBINADO (DEMOSTRACIÓN)"),
        demostracion = demostracion,
        inspecciones = listOf(
            inspeccionarClase(Pedido::class),
            inspeccionarClase(Producto::class),
            inspeccionarClase(Cliente::class)
        ),
        pedidosReales = pedidosDelDia.size
    )

    println("\n$resumen")
    guardarResumenConManejoDeError(resumen)
}
