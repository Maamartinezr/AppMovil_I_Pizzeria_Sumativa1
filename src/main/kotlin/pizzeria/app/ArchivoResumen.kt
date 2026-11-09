package pizzeria.app

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

const val ARCHIVO_RESUMEN = "resumen_pedidos.txt"

fun guardarResumen(texto: String): Path {
    val ruta = Path.of(ARCHIVO_RESUMEN)
    Files.writeString(ruta, texto, StandardCharsets.UTF_8)
    return ruta.toAbsolutePath()
}

fun guardarResumenConManejoDeError(texto: String) {
    try {
        val ruta = guardarResumen(texto)
        println("\nArchivo de resumen generado correctamente: $ruta")
    } catch (error: IOException) {
        System.err.println("No se pudo guardar $ARCHIVO_RESUMEN: ${error.message}")
    }
}
