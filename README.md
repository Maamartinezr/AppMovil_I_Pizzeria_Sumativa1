# Pizzería Kotlin — Proyecto final

Aplicación de consola educativa que simula funciones centrales de una pizzería: registrar clientes y pedidos, validar datos, verificar inventario, calcular precios y promociones, asignar repartidores y generar un resumen en consola y archivo de texto. El proyecto evolucionó en tres partes, aplicando progresivamente fundamentos y utilidades avanzadas de Kotlin.

> **Alcance:** esta entrega es un prototipo ejecutable en la JVM, no una aplicación Android de producción. Clientes, productos, inventario y pedidos se conservan en memoria solo durante la ejecución. El programa genera `resumen_pedidos.txt` en la carpeta desde donde se ejecuta.

## Contenido

- [Requisitos](#requisitos)
- [Estructura](#estructura)
- [Compilación y ejecución](#compilación-y-ejecución)
- [Uso del programa](#uso-del-programa)
- [Evolución de las tres partes](#evolución-de-las-tres-partes)
- [Validaciones y rangos](#validaciones-y-rangos)
- [Reglas de negocio del prototipo](#reglas-de-negocio-del-prototipo)
- [Arquitectura propuesta para una plataforma real](#arquitectura-propuesta-para-una-plataforma-real)
- [Pruebas manuales sugeridas](#pruebas-manuales-sugeridas)
- [Limitaciones y mejoras futuras](#limitaciones-y-mejoras-futuras)
- [Autoría](#autoría)

## Requisitos

- JDK 17 o superior.
- Gradle 8.x instalado para compilar desde terminal.
- Visual Studio Code con extensiones de Kotlin y Java recomendadas.
- Conexión a Internet la primera vez que Gradle descargue Kotlin y sus dependencias.

> **Nota sobre Gradle:** se utiliza Gradle porque la Parte 3 incluye Kotlin Reflection (`kotlin.reflect.full`), que requiere la dependencia `kotlin-reflect`. Gradle resuelve e incluye esta dependencia automáticamente, evitando configurar manualmente el classpath. No es un requisito explícito de la actividad, sino una manera práctica de ejecutar la funcionalidad de Reflection solicitada.

## Estructura

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

## Compilación y ejecución

Desde la carpeta del repositorio:

```bash
gradle run
```

Para compilar el proyecto:

```bash
gradle build
```

El proyecto declara Kotlin Reflection en Gradle para inspeccionar anotaciones, métodos y propiedades. También se podría compilar con `kotlinc`, pero habría que incluir `kotlin-reflect` manualmente en el classpath; por eso se recomienda Gradle para esta entrega.

## Uso del programa

Al iniciar, la consola solicita:

1. Nombre y dirección del cliente.
2. Teléfono internacional con formato `+XX (XXX) XXX-XXXX`.
3. Correo con formato `nombre@dominio.com`.
4. Nombre y precio de la pizza.
5. Cantidad de pizzas, entre 1 y 100.
6. Fecha del pedido con formato `AAAA-MM-DD` (por ejemplo, `2026-06-15`).
7. Notas opcionales del pedido.

Si el correo, el teléfono, la fecha o la cantidad no cumplen el formato/rango esperado, el programa muestra un mensaje y vuelve a solicitar el dato. Las fechas deben existir en el calendario; por ejemplo, `2026-02-30` se rechaza. Si la fecha es válida pero está fuera del periodo promocional, el pedido puede continuar, pero no recibe el descuento promocional.

Clientes y productos se registran en conjuntos (`MutableSet`) utilizando `equals()` y `hashCode()` como claves de negocio. Clientes se identifican por nombre y correo; productos por nombre y categoría. La misma función de registro rechaza duplicados, y el programa demuestra ese rechazo en la ejecución.

Después se verifica el inventario, se calculan los importes, se imprime el resumen en la terminal y se guarda en `resumen_pedidos.txt`. El archivo se crea o sobrescribe en el directorio desde donde se ejecuta `gradle run`.

## Evolución de las tres partes

### Parte 1 — Fundamentos y primera solución

Se modelaron los datos esenciales de cliente, pizza y pedido con variables y tipos adecuados (`String`, `Int`, `Double`, `val` y `var`). Se incorporó entrada/salida por consola, valores predeterminados ante entradas vacías o incorrectas, cálculo del total y descuento del 10 % al pedir más de tres pizzas. También se agregó una decisión para revisar disponibilidad y actualizar el stock, seguida de un resumen para cliente y pizzería.

### Parte 2 — Utilidades de Kotlin y organización del negocio

La solución avanzó hacia funciones reutilizables, colecciones y POO. Se añadieron clases para usuarios, clientes, repartidores, productos y pedidos; inventario con mapas; listas de repartidores; selección del más cercano; reportes y filtros de pedidos. Se incluyeron validaciones de correo/teléfono, valores nulos opcionales, manejo de errores numéricos, cálculo de impuestos y una lógica de reabastecimiento descriptiva.

### Parte 3 — Kotlin avanzado y demostración integrada

Se agregaron expresiones regulares para validar correo y teléfono, rangos para cantidades y fechas promocionales, y la anotación `@Promocionable`, consultada mediante Reflection. `Pedido` sobrecarga `+` para combinar pedidos del mismo cliente y volver a calcular sus importes; `Producto` y `Cliente` comparan igualdad por sus claves de negocio y tienen `hashCode()` coherente. Los registros reales en `MutableSet` impiden duplicados. Se implementó desestructuración de ambos modelos, se comparan `==` y `===`, y el programa guarda en archivo de texto el resumen integrado que presenta en consola, junto con la demostración y los resultados de Reflection.

## Validaciones y rangos

- **Correo:** se valida con Regex; se vuelve a solicitar hasta ingresar un formato aceptado.
- **Teléfono:** se valida con Regex y se normaliza al formato `+XX (XXX) XXX-XXXX`; se vuelve a solicitar si es incorrecto.
- **Cantidad:** solo se aceptan números enteros en el rango inclusivo `1..100`. Entradas vacías, no numéricas o fuera de rango muestran un mensaje claro y se solicitan nuevamente.
- **Fecha del pedido:** se ingresa como `AAAA-MM-DD` y se convierte a `LocalDate`. El programa vuelve a solicitarla si el formato es incorrecto o la fecha no existe.
- **Fecha promocional:** el pedido se compara con el rango inclusivo `RANGO_PROMOCIONAL` (del 1 de enero al 31 de diciembre de 2026). Una fecha válida fuera de ese intervalo no detiene el pedido, pero desactiva el descuento promocional y se muestra un aviso.

## Reglas de negocio del prototipo

- Cada pizza consume una unidad de cada ingrediente de su receta (masa, salsa y queso).
- El inventario inicial de ejemplo es de 100 unidades por ingrediente. Se descuenta solo si todos los ingredientes están disponibles para la cantidad solicitada.
- El ejemplo usa un impuesto del 19 %, calculado sobre el subtotal después de descuentos. **No es una recomendación tributaria**: la tasa y la base imponible deben ser confirmadas para el país y el negocio.
- Se aplica 10 % de descuento al subtotal cuando el pedido contiene más de tres pizzas.
- Los productos anotados con `@Promocionable` pueden recibir 15 % sobre su subtotal entre el 1 de enero y el 31 de diciembre de 2026. En este ejercicio, el descuento por cantidad y la promoción se acumulan, con límite máximo igual al subtotal. Para producción, deben definirse reglas de prioridad y acumulación sin ambigüedad.
- El repartidor se elige de una lista fija en memoria según distancia. No se calcula una ruta ni disponibilidad en tiempo real.
- La confirmación por correo es solo un mensaje de consola; no se envía un correo real.
- El pedido combinado se muestra como demostración y no vuelve a reservar ingredientes ni se añade al registro original.
- La unicidad de clientes y productos se mantiene solo en memoria durante la ejecución; no persiste entre ejecuciones.

## Arquitectura propuesta para una plataforma real

Este prototipo puede servir como ejercicio de lógica, pero una aplicación móvil requiere separar la interfaz, el dominio y los servicios. Una evolución razonable sería:

### Aplicación Android

- Crear un proyecto Android con Kotlin y Gradle; usar Jetpack Compose para pantallas de registro, catálogo, carrito, seguimiento y administración.
- Usar `ViewModel` y `StateFlow` para el estado de pantalla; las pantallas no deberían calcular precios ni editar inventario directamente.
- Validar datos también en el servidor. Las validaciones del cliente mejoran la experiencia, pero no son una barrera de seguridad.
- Configurar navegación, permisos mínimos, accesibilidad, estados de carga/error y pruebas de interfaz.

### Backend y datos

- Exponer una API HTTPS (por ejemplo, REST) para clientes, catálogo, pedidos, inventario y repartidores.
- Mantener precios, descuentos, impuestos e inventario como autoridad en el servidor; el total enviado por el móvil nunca debe aceptarse sin recalcularlo.
- Persistir datos en una base relacional como PostgreSQL. Las operaciones de crear pedido y reservar stock deben ejecutarse en una transacción para evitar sobreventa ante pedidos simultáneos.
- Guardar pedidos con estados definidos —por ejemplo, `RECIBIDO`, `EN_PREPARACION`, `LISTO`, `EN_CAMINO`, `ENTREGADO`, `CANCELADO`— y registrar sus cambios para auditoría.
- Modelar promociones con reglas configurables y fechas con zona horaria del negocio. Mantener el dinero en unidades menores enteras (por ejemplo, centavos) o con decimal exacto, no en `Double`.

### Reparto, notificaciones y operación

- Asignar repartidores con información de disponibilidad y ubicación, respetando privacidad y permisos; integrar un proveedor de mapas solo si es necesario.
- Enviar notificaciones mediante un servicio del backend (correo, SMS o Firebase Cloud Messaging), guardando el resultado y reintentando fallos de forma segura.
- Integrar pagos a través de un proveedor certificado; no almacenar datos de tarjetas en la aplicación.
- Proteger API y datos con autenticación, autorización por rol, TLS, almacenamiento seguro de secretos, validación de entradas, límites de solicitudes y registros sin datos personales innecesarios.
- Añadir monitoreo, copias de seguridad, migraciones de base de datos, pruebas automatizadas y despliegues por entornos.

### Separación de responsabilidades sugerida

```text
Android (Compose / ViewModel)
        │ HTTPS
        ▼
API / casos de uso (crear pedido, cotizar, reservar stock)
        ├── Base de datos
        ├── Servicio de notificaciones
        ├── Proveedor de pagos
        └── Servicio de asignación y seguimiento de reparto
```

## Pruebas manuales sugeridas

- Ingresar un correo mal formado y comprobar que se solicita de nuevo; luego ingresar uno válido.
- Probar teléfono válido e inválido; confirmar formato normalizado y nuevo intento ante error.
- Probar cantidades `0`, `101` y texto no numérico para comprobar el mensaje y el reintento; probar también `1` y `100` como límites válidos.
- Ingresar fecha mal formada como `15/06/2026` y fecha inexistente como `2026-02-30`; verificar que el programa informa el error y vuelve a solicitarla.
- Ingresar `2026-06-15`; debe aceptarse como fecha dentro del periodo promocional.
- Ingresar `2027-01-10`; debe aceptarse como fecha válida fuera del periodo, informar que la promoción no aplica y continuar el pedido.
- Probar precio no numérico y precio negativo; verificar los mensajes de corrección.
- Solicitar más ingredientes que los disponibles modificando temporalmente el inventario inicial; verificar que no se descuenta parcialmente el stock.
- Probar un pedido de cuatro pizzas para observar el descuento por cantidad.
- Comprobar que la salida indique que los duplicados fueron rechazados y que el tamaño de los registros no aumente.
- Ejecutar `gradle run` y verificar el resumen en consola y la creación de `resumen_pedidos.txt` en el directorio de trabajo.
- Revisar que el archivo contenga el resumen, la demostración de la Parte 3 y los resultados de Reflection.

## Limitaciones y mejoras futuras

Esta entrega no es todavía una app móvil Android: es un programa JVM de consola. No persiste datos entre ejecuciones, no tiene API o base de datos, no envía notificaciones reales, no consulta mapas ni procesa pagos. Como siguiente paso académico, se recomienda separar el dominio en archivos, agregar pruebas unitarias para registro, validaciones, reglas de precio e inventario y crear una interfaz Android. Para uso comercial, deben revisarse tasas de impuesto, privacidad, requisitos legales y reglas de promoción con responsables del negocio.

## Autoría

**María Alejandra Martínez Reverol**  
Proyecto desarrollado para la asignatura **Aplicaciones Móviles I**.
