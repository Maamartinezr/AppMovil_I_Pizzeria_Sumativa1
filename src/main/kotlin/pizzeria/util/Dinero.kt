package pizzeria.util

import java.util.Locale

fun dinero(valor: Double): String =
    String.format(Locale("es", "CL"), "%,.0f", valor)
