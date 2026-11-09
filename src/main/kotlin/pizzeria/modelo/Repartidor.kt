package pizzeria.modelo

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
