package pizzeria.util

import pizzeria.modelo.Cliente
import pizzeria.modelo.Producto

/** Agrega el cliente al registro o informa que ya existe la misma clave de negocio. */
fun registrarCliente(cliente: Cliente, registro: MutableSet<Cliente>): Boolean {
    if (!registro.add(cliente)) {
        println("Registro rechazado: ya existe un cliente con el mismo nombre y correo.")
        return false
    }
    println("Cliente registrado correctamente: ${cliente.nombre}.")
    return true
}

/** Agrega el producto al catálogo o informa que ya existe el mismo nombre y categoría. */
fun registrarProducto(producto: Producto, registro: MutableSet<Producto>): Boolean {
    if (!registro.add(producto)) {
        println("Registro rechazado: ya existe un producto con el mismo nombre y categoría.")
        return false
    }
    println("Producto registrado correctamente: ${producto.nombre}.")
    return true
}
