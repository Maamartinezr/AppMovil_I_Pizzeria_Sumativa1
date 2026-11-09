package pizzeria.util

private const val TELEFONO_PREDETERMINADO = "+00 (000) 000-0000"
private const val CORREO_PREDETERMINADO = "correo@invalido.com"
private val CORREO_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val TELEFONO_REGEX = Regex("^\\+?([0-9]{1,3})[ .-]?\\(?([0-9]{3})\\)?[ .-]?([0-9]{3})[ .-]?([0-9]{4})$")

fun leerTexto(mensaje: String, predeterminado: String): String {
    print(mensaje)
    return readLine()?.trim()?.takeIf(String::isNotEmpty) ?: predeterminado
}

fun leerCorreo(): String {
    while (true) {
        print("Correo electrónico: ")
        val correo = readLine()?.trim() ?: return CORREO_PREDETERMINADO
        if (correo.matches(CORREO_REGEX)) return correo
        println("Correo inválido. Ingresa un formato como nombre@dominio.com.")
    }
}

fun formatearTelefono(entrada: String): String? {
    val match = TELEFONO_REGEX.matchEntire(entrada.trim()) ?: return null
    val (codigoPais, area, prefijo, linea) = match.destructured
    return "+$codigoPais ($area) $prefijo-$linea"
}

fun leerTelefono(): String {
    while (true) {
        print("Teléfono (ejemplo: +56 (912) 345-6789): ")
        val entrada = readLine() ?: return TELEFONO_PREDETERMINADO
        formatearTelefono(entrada)?.let { return it }
        println("Teléfono inválido. Reingrésalo en formato +XX (XXX) XXX-XXXX.")
    }
}

fun leerCantidad(): Int {
    while (true) {
        print("Cantidad de pizzas (1-100): ")
        val cantidad = readLine()?.trim()?.toIntOrNull() ?: run {
            println("Ingresa un número entero entre 1 y 100.")
            continue
        }
        if (cantidad in 1..100) return cantidad
        println("Cantidad fuera de rango. Ingresa un valor entre 1 y 100.")
    }
}

fun leerPrecio(): Double {
    while (true) {
        print("Precio por pizza: $")
        val entrada = readLine() ?: return 0.0
        try {
            val precio = entrada.trim().replace(',', '.').toDouble()
            require(precio.isFinite() && precio >= 0.0) { "El precio debe ser válido y no negativo." }
            return precio
        } catch (_: NumberFormatException) {
            println("Costo inválido. Ingresa un número, por ejemplo 8500.")
        } catch (error: IllegalArgumentException) {
            println("Costo no válido: ${error.message}")
        }
    }
}
