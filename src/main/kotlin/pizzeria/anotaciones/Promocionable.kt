package pizzeria.anotaciones

/** Marca los productos que pueden participar en una promoción especial. */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(val descripcion: String = "Promoción especial")
