# Pizzería Kotlin — Proyecto final

Aplicación de consola educativa que simula funciones centrales de una pizzería: registrar clientes y pedidos, validar datos, verificar inventario, calcular precios y promociones, asignar repartidores y mostrar reportes. El proyecto evolucionó en tres partes, aplicando progresivamente fundamentos y utilidades avanzadas de Kotlin.

> **Alcance:** esta entrega es un prototipo ejecutable en la JVM, no una aplicación Android de producción. El inventario, los repartidores y los pedidos viven solo en memoria durante la ejecución. El README incluye una propuesta para llevar la solución a una plataforma real.

## Contenido

- [Requisitos](#requisitos)
- [Estructura](#estructura)
- [Compilación y ejecución](#compilación-y-ejecución)
- [Uso del programa](#uso-del-programa)
- [Evolución de las tres partes](#evolución-de-las-tres-partes)
- [Reglas de negocio del prototipo](#reglas-de-negocio-del-prototipo)
- [Arquitectura propuesta para una plataforma real](#arquitectura-propuesta-para-una-plataforma-real)
- [Pruebas manuales sugeridas](#pruebas-manuales-sugeridas)
- [Limitaciones y mejoras futuras](#limitaciones-y-mejoras-futuras)

## Requisitos

- JDK 17 o superior.
- Gradle 8.x instalado para compilar desde terminal.
- Visual Studio Code con extensiones de Kotlin y Java recomendadas.
- Conexión a Internet la primera vez que Gradle descargue Kotlin y sus dependencias.

> Nota importante sobre Gradle: se utiliza Gradle porque la Parte 3 incluye Kotlin Reflection (`kotlin.reflect.full`), la cual requiere la dependencia `kotlin-reflect`. Al compilar con Gradle, esa dependencia se resuelve automáticamente y evita errores de classpath. En otras palabras, Gradle no es un requisito arbitrario del profesor, sino una solución práctica para que el proyecto funcione correctamente con la funcionalidad avanzada solicitada.

## Estructura

```text
.
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── src/
    └── main/kotlin/
        └── Main.kt
```

El código se mantiene en `Main.kt` para facilitar la revisión de la actividad. En un desarrollo más amplio convendría separar modelos, servicios de negocio, persistencia e interfaz.

## Compilación y ejecución

Desde la carpeta del repositorio:

```bash
gradle run
```

Para compilar un artefacto ejecutable:

```bash
gradle build
```

El proyecto usa Gradle para declarar explícitamente Kotlin Reflection, necesaria para inspeccionar anotaciones, métodos y propiedades. También se puede compilar con `kotlinc`, pero hay que incluir `kotlin-reflect` en el classpath; por eso se recomienda Gradle.

## Uso del programa

Al iniciar, la consola solicita:

1. Nombre y dirección del cliente.
2. Teléfono internacional con el formato `+XX (XXX) XXX-XXXX`.
3. Correo con formato `nombre@dominio.com`.
4. Nombre y precio de la pizza.
5. Cantidad entre 1 y 100 y notas opcionales.

Después valida y procesa el pedido, muestra si hay stock, presenta el resumen de cobro y ejecuta demostraciones de las herramientas avanzadas de la Parte 3. Por ejemplo, para el teléfono se puede ingresar `+56 (912) 345-6789`.

## Evolución de las tres partes

### Parte 1 — Fundamentos y primera solución

Se modelaron los datos esenciales de cliente, pizza y pedido con variables y tipos adecuados (`String`, `Int`, `Double`, `val` y `var`). Se incorporó entrada/salida por consola, valores predeterminados ante entradas vacías o incorrectas, cálculo del total y descuento del 10 % al pedir más de tres pizzas. También se agregó una decisión para revisar disponibilidad y actualizar el stock, seguida de un resumen para cliente y pizzería.

### Parte 2 — Utilidades de Kotlin y organización del negocio

La solución avanzó hacia funciones reutilizables, colecciones y POO. Se añadieron clases para usuarios, clientes, repartidores, productos y pedidos; inventario con mapas; listas de repartidores; selección del más cercano; reportes y filtros de pedidos. Se incluyeron validaciones de correo/teléfono, valores nulos opcionales, manejo de errores numéricos, cálculo de impuestos y una lógica de reabastecimiento descriptiva.

### Parte 3 — Kotlin avanzado y demostración integrada

Se agregaron expresiones regulares para validar correo y teléfono, rangos para cantidades y fechas promocionales, y la anotación `@Promocionable`, consultada con Reflection. `Pedido` sobrecarga `+` para combinar pedidos del mismo cliente y volver a calcular sus importes; `Producto` y `Cliente` comparan igualdad por sus claves de negocio y tienen `hashCode()` coherente. Se implementó desestructuración de ambos modelos y una demostración que imprime pedidos combinados, productos promocionables, deduplicación y metadatos de clases.

## Reglas de negocio del prototipo

- Cada pizza consume una unidad de cada ingrediente de su receta (masa, salsa y queso).
- El inventario inicial de ejemplo es de 100 unidades por ingrediente. Se descuenta solo si todos los ingredientes están disponibles.
- El ejemplo usa un impuesto del 19 %, que se calcula sobre el subtotal después de descuentos. **No es una recomendación tributaria**: la tasa y la base imponible deben ser confirmadas para el país y el negocio.
- Se aplica 10 % de descuento al subtotal cuando el pedido contiene más de tres pizzas.
- Los productos anotados con `@Promocionable` pueden recibir 15 % sobre su propio subtotal entre el 1 de enero y el 31 de diciembre de 2026. En este ejercicio, el descuento por cantidad y la promoción se acumulan, con límite máximo igual al subtotal. Para producción, deben definirse reglas de prioridad y acumulación sin ambigüedad.
- El repartidor se elige de una lista fija en memoria según distancia. No se calcula una ruta ni disponibilidad en tiempo real.
- La confirmación por correo es solo un mensaje de consola; no se envía un correo real.
- La combinación de pedidos se muestra como demostración y no vuelve a reservar ingredientes ni añade el pedido de demostración al registro original.

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
- Probar teléfono válido, mal formado y fin de entrada para verificar formato y valor predeterminado.
- Probar cantidades `0`, `101` y una válida como `4`.
- Probar precio no numérico y precio negativo; verificar los mensajes de corrección.
- Solicitar más ingredientes que los disponibles modificando temporalmente el inventario inicial; verificar que no se descuenta parcialmente el stock.
- Probar un pedido de cuatro pizzas para observar el descuento por cantidad.
- Revisar la sección de demostración para comprobar combinación, desestructuración, anotación, igualdad y Reflection.

## Limitaciones y mejoras futuras

Esta entrega no es todavía una app móvil Android: es un programa JVM de consola. No persiste datos entre ejecuciones, no tiene API o base de datos, no envía notificaciones reales, no consulta mapas ni procesa pagos. Como siguiente paso académico, se recomienda separar el dominio en archivos, agregar pruebas unitarias para reglas de precio e inventario y crear una interfaz Android. Para uso comercial, deben revisarse tasas de impuesto, privacidad, requisitos legales y reglas de promoción con responsables del negocio.
