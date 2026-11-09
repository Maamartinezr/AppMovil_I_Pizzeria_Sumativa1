# Mi primera app en Kotlin: Pizzería

Implementación de consola de la Parte 1 de la actividad. El programa registra un cliente, recibe los datos de una pizza y del pedido, calcula un descuento del 10 % cuando se solicitan más de tres pizzas, comprueba y actualiza el inventario, asigna un repartidor de ejemplo y presenta un resumen.

## Requisitos

- JDK instalado.
- Compilador Kotlin (`kotlinc`) disponible en la terminal de Visual Studio Code.

## Ejecutar

Desde la raíz del repositorio:

```bash
kotlinc src/main/kotlin/Main.kt -include-runtime -d pizzeria.jar
java -jar pizzeria.jar
```

El inventario del ejemplo se expresa en unidades de ingredientes, donde cada pizza consume una unidad. La asignación de repartidor es ilustrativa; la Parte 1 no incluye todavía una lista de repartidores ni una lógica de disponibilidad.
