# Pizzería Kotlin — Proyecto final

Aplicación de consola educativa que simula funciones centrales de una pizzería: registrar clientes y pedidos, validar datos, verificar inventario, calcular precios y promociones, asignar repartidores y generar un resumen en consola y archivo de texto. El proyecto evolucionó en tres partes aplicando progresivamente fundamentos y utilidades avanzadas de Kotlin.

> **Alcance:** prototipo JVM de consola, no aplicación Android de producción. Clientes, productos, inventario y pedidos se conservan en memoria solo durante la ejecución. El programa crea `resumen_pedidos.txt` en la carpeta desde donde se ejecuta.

## Requisitos y ejecución

- JDK 17 o superior.
- Gradle 8.x.
- Visual Studio Code con extensiones para Kotlin/Java.

Gradle se mantiene porque el requerimiento de la Parte 3 utiliza Kotlin Reflection (`kotlin.reflect.full`) para examinar las clases. La dependencia `kotlin-reflect` se configura en Gradle para resolver el classpath automáticamente; no es un requerimiento independiente de la actividad, sino una forma práctica de ejecutar Reflection.

Desde la raíz del proyecto:

```bash
gradle run
```

Para compilar:

```bash
gradle build
```

## Estructura del proyecto

La organización sigue el ejemplo solicitado en clases, usando el paquete raíz `pizzeria` y separando responsabilidades en `anotaciones`, `app`, `modelo` y `util`.

## Uso del programa

El programa solicita nombre y dirección, teléfono internacional, correo, pizza, precio, cantidad de pizzas (1–100), fecha del pedido y notas opcionales. Un correo, teléfono o fecha inválidos se solicitan nuevamente. La fecha debe ingresarse en formato ISO `AAAA-MM-DD` y corresponder a una fecha real (por ejemplo, `2026-06-15`). Si la fecha es válida pero está fuera del periodo promocional, el pedido puede continuar, pero no se aplica el descuento de promoción.

Clientes y productos se registran en conjuntos (`MutableSet`) utilizando `equals()` y `hashCode()` como claves de negocio. Clientes se identifican por nombre y correo; productos por nombre y categoría. Los intentos repetidos se rechazan.

El programa valida el stock, calcula importes, muestra el resumen en la terminal y lo guarda en `resumen_pedidos.txt`.

## Validaciones de entrada y rangos

- Correo electrónico: Regex; vuelve a solicitarse hasta recibir un formato válido.
- Teléfono: Regex y normalización al formato `+XX (XXX) XXX-XXXX`; vuelve a solicitarse ante errores.
- Fecha: analiza `AAAA-MM-DD` como `LocalDate`; rechaza formatos inválidos y fechas inexistentes, indicando cómo corregirlas.
- Cantidad de pizzas: solo acepta enteros incluidos en el rango `1..100`; muestra un mensaje claro y vuelve a pedir el dato en caso de error.
- Fecha promocional: el rango inclusivo se define en `RANGO_PROMOCIONAL`; fuera de él el pedido continúa sin el descuento promocional.

## Reglas de negocio de ejemplo

- Cada pizza requiere una unidad de masa, salsa y queso; el inventario inicial es de 100 unidades de cada ingrediente.
- El stock solo se descuenta si todos los ingredientes están disponibles para la cantidad solicitada.
- Se usa un impuesto ilustrativo del 19 %, calculado sobre el subtotal después de descuentos; confirmar la regla fiscal aplicable antes de cualquier uso real.
- Se aplica 10 % por cantidad al pedir más de tres pizzas.
- Los productos con `@Promocionable` reciben una promoción de ejemplo del 15 % durante 2026. La promoción y el descuento por cantidad se acumulan, con límite total hasta el subtotal.
- Los repartidores y distancias son datos ilustrativos; no se consulta ubicación real.
- Las notificaciones son mensajes de consola, no correos enviados.

## Verificación manual de fechas y cantidades

- Introducir cantidad `0`, `101` o texto; el programa debe mostrar error y volver a solicitarla. Probar también `1` y `100` como límites válidos.
- Introducir una fecha con formato incorrecto, como `15/06/2026`, o una fecha inexistente, como `2026-02-30`; debe mostrar error y volver a solicitarla.
- Introducir `2026-06-15`; debe aceptarse y estar dentro del rango promocional configurado.
- Introducir una fecha real fuera de 2026, como `2027-01-10`; debe aceptarse con aviso y no aplicar el descuento promocional.

## Propuesta para una plataforma real

Para convertir el prototipo en una app móvil real, se recomienda usar Android con Kotlin y Jetpack Compose, separar UI de lógica mediante ViewModel/StateFlow, delegar precios/promociones/inventario a una API HTTPS, persistir datos con transacciones en una base de datos, usar estados controlados de pedido, integrar notificaciones/pagos/mapas de proveedores adecuados y agregar seguridad, pruebas y monitoreo.

## Limitaciones

La solución no conserva datos entre ejecuciones y no incluye interfaz Android, API, base de datos, notificaciones reales, pagos ni ubicación. Es un prototipo didáctico para ejecutar en consola y mostrar los conceptos solicitados.

## Autoría

**María Alejandra Martínez Reverol**  
Proyecto desarrollado para la asignatura **Aplicaciones Móviles I**.
