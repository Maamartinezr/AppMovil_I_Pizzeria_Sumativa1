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

La organización sigue el ejemplo solicitado en clases, usando el paquete raíz `pizzeria` y separando responsabilidades en `anotaciones`, `app`, `modelo` y `util`:

```text
.
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── src/main/kotlin/
    └── pizzeria/
        ├── anotaciones/
        │   └── Promocionable.kt
        ├── app/
        │   ├── ArchivoResumen.kt
        │   ├── Main.kt
        │   └── Resumen.kt
        ├── modelo/
        │   ├── Cliente.kt
        │   ├── Pedido.kt
        │   ├── Producto.kt
        │   ├── ProductoPedido.kt
        │   ├── Repartidor.kt
        │   └── Usuario.kt
        └── util/
            ├── Dinero.kt
            ├── Inspector.kt
            ├── Inventario.kt
            ├── Promociones.kt
            ├── Registros.kt
            └── Validaciones.kt
```

- `pizzeria.anotaciones`: anotaciones personalizadas del dominio.
- `pizzeria.app`: punto de entrada, presentación y escritura del resumen.
- `pizzeria.modelo`: entidades y reglas propias del cliente, producto, pedido y repartidor.
- `pizzeria.util`: validaciones, inventario, registro sin duplicados, promociones, formato monetario e inspección con Reflection.

## Uso del programa

El programa solicita nombre y dirección, teléfono internacional, correo, pizza, precio, cantidad (1–100) y notas opcionales. Un correo o teléfono inválido se solicita nuevamente. El teléfono se normaliza al formato `+XX (XXX) XXX-XXXX`.

Clientes y productos se registran en conjuntos (`MutableSet`) utilizando `equals()` y `hashCode()` como claves de negocio. Clientes se identifican por nombre y correo; productos por nombre y categoría. El flujo demuestra que intentos repetidos se rechazan sin incrementar el registro. Como el ejemplo solo captura una alta de cada tipo por ejecución, esa verificación se ejecuta como demostración de la misma función de registro.

El programa valida el stock, calcula importes, muestra el resumen en la terminal y lo guarda en `resumen_pedidos.txt`. El archivo se crea o sobrescribe en el directorio desde donde se ejecuta `gradle run`.

## Evolución de las tres partes

### Parte 1 — Fundamentos

Modelado inicial de cliente, pizza y pedido con tipos apropiados, entrada/salida de consola, valores por defecto, cálculo del total y descuento por cantidad, verificación básica del inventario y resumen del pedido.

### Parte 2 — Utilidades y organización del negocio

Funciones reutilizables, colecciones, mapa de inventario, lista de repartidores, asignación por cercanía, manejo de errores y nulos, impuestos, descuentos, clases y herencia para representar usuarios y entidades del negocio.

### Parte 3 — Kotlin avanzado

Regex y rangos para validaciones y promociones; anotación `@Promocionable` y Reflection; sobrecarga de `+` para combinar pedidos; desestructuración de cliente y pedido; `equals()`/`hashCode()` y registros sin duplicados; inspección dinámica de propiedades y métodos; y generación del resumen tanto en consola como en `.txt`.

## Reglas de negocio de ejemplo

- Cada pizza requiere una unidad de masa, salsa y queso; el inventario de inicio es de 100 unidades de cada ingrediente.
- El stock solo se descuenta si todos los ingredientes están disponibles para la cantidad solicitada.
- Se usa un impuesto ilustrativo del 19 %, calculado sobre el subtotal después de descuentos; confirmar la regla fiscal aplicable antes de cualquier uso real.
- Se aplica 10 % por cantidad al pedir más de tres pizzas.
- Los productos con `@Promocionable` reciben una promoción de ejemplo del 15 % durante 2026. La promoción y el descuento por cantidad se acumulan, con límite total hasta el subtotal.
- Los repartidores y distancias son datos ilustrativos; no se consulta ubicación real.
- Las notificaciones son mensajes de consola, no correos enviados.
- El pedido combinado es una demostración y no vuelve a reservar inventario ni se agrega al registro real de pedidos.

## Propuesta para una plataforma real

Para convertir el prototipo en una app móvil real, se recomienda:

1. Crear la aplicación Android en Kotlin, usando Jetpack Compose para interfaz y ViewModel/StateFlow para gestionar el estado de pantalla.
2. Separar la aplicación móvil de una API HTTPS que sea la autoridad para precios, promociones e inventario.
3. Persistir clientes, catálogo, pedidos e inventario en una base de datos. Reservar stock y crear el pedido dentro de una transacción para evitar sobreventa en pedidos concurrentes.
4. Modelar los estados del pedido con un `enum` o estados controlados y registrar cada cambio.
5. Integrar proveedores reales de correo/notificaciones, pagos y mapas únicamente desde servicios apropiados, protegiendo credenciales y datos personales.
6. Usar decimal exacto o unidades monetarias menores enteras para importes, y configurar promociones e impuestos según las reglas del negocio y jurisdicción.
7. Incorporar autenticación, permisos por rol, pruebas automatizadas, monitoreo, copias de seguridad y manejo de errores de red.

```text
Android (Compose / ViewModel) --HTTPS--> API / casos de uso
                                          ├── Base de datos
                                          ├── Notificaciones
                                          ├── Pagos
                                          └── Reparto y seguimiento
```

## Verificación manual

- Probar correo inválido y después válido; repetir con teléfono.
- Probar cantidades 0, 101 y 1–100.
- Ingresar precio no numérico o negativo.
- Solicitar más stock del disponible y verificar que no se descuente parcialmente.
- Revisar en la salida que los intentos duplicados se rechacen y los tamaños de los conjuntos sigan en uno.
- Ejecutar `gradle run`; comprobar el resumen en consola y que `resumen_pedidos.txt` aparezca en la carpeta de ejecución con los resúmenes, demostraciones y resultados de Reflection.

## Limitaciones

La solución no conserva datos entre ejecuciones y no incluye interfaz Android, API, base de datos, notificaciones reales, pagos ni ubicación. Es un prototipo didáctico para ejecutar en consola y mostrar los conceptos solicitados.

## Autoría

**María Alejandra Martínez Reverol**  
Proyecto desarrollado para la asignatura **Aplicaciones Móviles I**.
