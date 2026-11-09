# Aplicación de gestión para pizzería en Kotlin

Proyecto de consola desarrollado progresivamente a lo largo de las 3 semanas de la 
Experiencia 1.
Implementa registro de clientes y pedidos, inventario, promociones y conceptos avanzados de Kotlin.

## Requisitos

- JDK instalado.
- Compilador Kotlin (`kotlinc`) en la terminal de Visual Studio Code.
- Para la reflexión con `kotlin.reflect.full`, usar un compilador/entorno Kotlin que incluya `kotlin-reflect`.

## Compilar y ejecutar

```bash
kotlinc src/main/kotlin/Main.kt -include-runtime -d pizzeria.jar
java -jar pizzeria.jar
```

Si la instalación de Kotlin no incluye la biblioteca de reflexión, agrega `kotlin-reflect.jar` al classpath al compilar y ejecutar.

## Contenido de la Sumativa 1

- Regex para validar correos y validar/formatear teléfonos; los campos se vuelven a solicitar ante un formato incorrecto.
- Rangos `1..100` para la cantidad de pizzas y un rango de fechas promocional para el año 2026.
- Anotación personalizada `@Promocionable`, retenida en runtime y consultada mediante reflexión para aplicar la promoción.
- Inspección dinámica de propiedades y métodos de `Pedido`, `Producto` y `Cliente`.
- Operador `+` en `Pedido` para combinar pedidos del mismo cliente, agrupar sus líneas de producto y recalcular subtotal, impuestos y descuentos.
- Igualdad y `hashCode()` personalizados para evitar duplicados de clientes (nombre/correo) y productos (nombre/categoría).
- Componentes para desestructurar clientes y pedidos; demostraciones de objetos iguales y pedidos combinados en la consola.

La receta, el stock, la tasa de impuesto, la tasa promocional y las fechas de promoción son parámetros ilustrativos, que pueden ajustarse a las reglas reales del negocio. La promoción anotada se aplica cuando el pedido está dentro del periodo establecido y contiene un producto marcado como promocionable.
