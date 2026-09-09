package co.edu.udea.compumovil.labscm20262_gr03.navigation

import android.content.Intent

/**
 * AppNavigation
 *
 * Punto central de la navegación entre PersonalDataActivity y
 * ContactDataActivity. Define las claves de los Intent extras y el
 * modelo de datos personales que viaja entre ambas Activities.
 */
object DatosPersonalesExtras {
    const val NOMBRES = "extra_nombres"
    const val APELLIDOS = "extra_apellidos"
    const val SEXO = "extra_sexo"
    const val FECHA_NACIMIENTO = "extra_fecha_nacimiento"
    const val GRADO_ESCOLARIDAD = "extra_grado_escolaridad"
}

/**
 * Datos de la pantalla de información personal, ya recibidos y listos
 * para usarse en ContactDataActivity (validación final + log).
 */
data class DatosPersonalesRecibidos(
    val nombres: String = "",
    val apellidos: String = "",
    val sexo: String = "",
    val fechaNacimiento: String = "",
    val gradoEscolaridad: String = ""
) {
    val sonValidos: Boolean
        get() = nombres.isNotBlank() &&
                apellidos.isNotBlank() &&
                fechaNacimiento.isNotBlank()
}

/**
 * Extrae los datos personales de un Intent recibido, usando las claves
 * definidas en DatosPersonalesExtras.
 */
fun Intent.obtenerDatosPersonales(): DatosPersonalesRecibidos {
    return DatosPersonalesRecibidos(
        nombres = getStringExtra(DatosPersonalesExtras.NOMBRES) ?: "",
        apellidos = getStringExtra(DatosPersonalesExtras.APELLIDOS) ?: "",
        sexo = getStringExtra(DatosPersonalesExtras.SEXO) ?: "",
        fechaNacimiento = getStringExtra(DatosPersonalesExtras.FECHA_NACIMIENTO) ?: "",
        gradoEscolaridad = getStringExtra(DatosPersonalesExtras.GRADO_ESCOLARIDAD) ?: ""
    )
}