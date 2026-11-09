data class Cliente(
    val nombre: String,
    val direccion: String,
    val telefono: String
)

data class Pizza(
    val nombrePizza: String,
    val precioUnitario: Double,
    var ingredientesDisponibles: Int
)

data class Pedido(
    val idPedido: Int,
    val cantidadPizzas: Int,
    var totalPedido: Double,
    var estadoPedido: String,
    val repartidorAsignado: String
)

fun leerTexto(mensaje: String, predeterminado: String): String {
    print(mensaje)
    return readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: predeterminado
}

fun leerEntero(mensaje: String, predeterminado: Int, minimo: Int): Int {
    while (true) {
        print(mensaje)
        val entrada = readLine()?.trim()
        val valor = entrada?.toIntOrNull()
        if (valor != null && valor >= minimo) return valor
        println("Entrada inválida. Ingresa un número entero mayor o igual a $minimo. Se usará $predeterminado si no hay más entrada.")
        if (entrada == null) return predeterminado
    }
}

fun leerPrecio(mensaje: String, predeterminado: Double): Double {
    while (true) {
        print(mensaje)
        val entrada = readLine()?.trim()?.replace(',', '.')
        val valor = entrada?.toDoubleOrNull()
        if (valor != null && valor >= 0.0) return valor
        println("Precio inválido. Debe ser un número igual o mayor que cero.")
        if (entrada == null) return predeterminado
    }
}

fun leerTelefono(): String {
    print("Teléfono de contacto: ")
    val entrada = readLine()?.trim()
    return if (!entrada.isNullOrEmpty() && entrada.matches(Regex("[+]?[- 0-9]{7,16}"))) {
        entrada
    } else {
        println("Teléfono vacío o inválido; se registrará 'No registrado'.")
        "No registrado"
    }
}

fun main() {
    println("=== Registro de pedido - Pizzería ===")
    val cliente = Cliente(
        nombre = leerTexto("Nombre del cliente: ", "Cliente sin nombre"),
        direccion = leerTexto("Dirección de entrega: ", "Dirección no registrada"),
        telefono = leerTelefono()
    )

    println("\n=== Datos de la pizza ===")
    val pizza = Pizza(
        nombrePizza = leerTexto("Nombre de la pizza: ", "Pizza especial"),
        precioUnitario = leerPrecio("Precio unitario: $", 0.0),
        ingredientesDisponibles = leerEntero("Unidades de ingredientes disponibles: ", 0, 0)
    )
    val cantidad = leerEntero("Cantidad de pizzas solicitadas: ", 1, 1)

    val subtotal = pizza.precioUnitario * cantidad
    val descuento = if (cantidad > 3) subtotal * 0.10 else 0.0
    val total = subtotal - descuento
    val pedido = Pedido(
        idPedido = 1,
        cantidadPizzas = cantidad,
        totalPedido = total,
        estadoPedido = "Preparación",
        repartidorAsignado = "Repartidor por asignar"
    )

    // Para este ejemplo, cada pizza consume una unidad del inventario de ingredientes.
    val mensajeStock = if (pizza.ingredientesDisponibles >= pedido.cantidadPizzas) {
        pizza.ingredientesDisponibles -= pedido.cantidadPizzas
        pedido.estadoPedido = "Confirmado - en preparación"
        pedido.repartidorAsignado = "Carlos (asignación de ejemplo)"
        "Pedido confirmado. Stock restante: ${pizza.ingredientesDisponibles} unidades."
    } else {
        pedido.estadoPedido = "No confirmado - stock insuficiente"
        "No se puede completar el pedido: ingredientes insuficientes. Disponibles: ${pizza.ingredientesDisponibles}; requeridos: ${pedido.cantidadPizzas}."
    }

    println("\n$mensajeStock")
    println("\n========== RESUMEN DEL PEDIDO ==========")
    println("ID del pedido: ${pedido.idPedido}")
    println("Cliente: ${cliente.nombre}")
    println("Dirección de entrega: ${cliente.direccion}")
    println("Teléfono: ${cliente.telefono}")
    println("Pizza: ${pizza.nombrePizza}")
    println("Precio unitario: $${"%.2f".format(pizza.precioUnitario)}")
    println("Cantidad: ${pedido.cantidadPizzas}")
    println("Subtotal: $${"%.2f".format(subtotal)}")
    println("Descuento: $${"%.2f".format(descuento)}${if (cantidad > 3) " (10%)" else " (no aplica)"}")
    println("Total: $${"%.2f".format(pedido.totalPedido)}")
    println("Estado: ${pedido.estadoPedido}")
    println("Repartidor asignado: ${pedido.repartidorAsignado}")
    println("========================================")
}
