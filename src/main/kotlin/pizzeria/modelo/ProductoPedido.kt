package pizzeria.modelo

data class ProductoPedido(
    val producto: Producto,
    val cantidad: Int
) {
    init {
        require(cantidad > 0) { "La cantidad debe ser positiva." }
    }
}
