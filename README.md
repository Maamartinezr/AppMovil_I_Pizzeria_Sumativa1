# Aplicación de gestión para pizzería en Kotlin

Proyecto de consola para desarrollar progresivamente la actividad en Kotlin. La implementación actual incorpora los requerimientos de la Parte 1 y la Parte 2: registro y validación de clientes, productos y pedidos; inventario por ingrediente; cálculo de impuestos y descuentos; asignación de repartidor; manejo de errores y resumen/reporte.

## Requisitos

- JDK instalado.
- Compilador Kotlin (`kotlinc`) en la terminal de Visual Studio Code.

## Ejecutar

Desde la raíz del repositorio:

```bash
kotlinc src/main/kotlin/Main.kt -include-runtime -d pizzeria.jar
java -jar pizzeria.jar
```

## Funcionalidades de la Parte 2

- Clases `Usuario`, `Cliente`, `Repartidor`, `Producto` y `Pedido`, con herencia y métodos asociados.
- Validación de teléfono y correo, con valores predeterminados cuando el formato no es correcto.
- Cálculo de subtotal, impuesto de ejemplo del 19 %, descuento del 10 % para más de tres pizzas y total final.
- Inventario en un `MutableMap`; la receta de ejemplo consume una unidad de masa, salsa y queso por pizza. El stock inicial es de 20 unidades por ingrediente.
- Verificación de existencias con `when`; si falta stock, se detallan los ingredientes y los pasos de reabastecimiento. El inventario solo se descuenta si el pedido puede prepararse.
- Arreglo de pedidos del día, reporte iterativo y agrupación por `filter` en completados y pendientes.
- Selección del repartidor disponible más cercano usando `sortedBy` y `find`.
- Validación de entradas numéricas con `try-catch`; notas opcionales con operadores seguros y valor predeterminado. Se muestra una notificación mediante `let` si el pedido tiene cliente registrado.

La tasa de impuesto, el inventario inicial y la receta son valores de ejemplo ajustables al caso real de la pizzería. Para probar varios pedidos del día, se puede ampliar el registro para crear más instancias de `Pedido` y agregarlas a `pedidosDelDia`.
