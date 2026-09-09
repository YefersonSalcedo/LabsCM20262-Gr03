package co.edu.udea.compumovil.labscm20262_gr03.ui.personal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PersonalData(
    val nombres: String = "",
    val apellidos: String = "",
    val sexo: String = "",
    val fechaNacimiento: String = "",
    val gradoEscolaridad: String = ""
)

class PersonalDataViewModel : ViewModel() {

    private val _nombres = MutableStateFlow("")
    val nombres: StateFlow<String> = _nombres.asStateFlow()

    private val _apellidos = MutableStateFlow("")
    val apellidos: StateFlow<String> = _apellidos.asStateFlow()

    private val _sexo = MutableStateFlow("")
    val sexo: StateFlow<String> = _sexo.asStateFlow()

    private val _fechaNacimiento = MutableStateFlow("")
    val fechaNacimiento: StateFlow<String> = _fechaNacimiento.asStateFlow()

    private val _gradoEscolaridad = MutableStateFlow("")
    val gradoEscolaridad: StateFlow<String> = _gradoEscolaridad.asStateFlow()

    fun actualizarNombres(valor: String) {
        _nombres.value = valor
    }

    fun actualizarApellidos(valor: String) {
        _apellidos.value = valor
    }

    fun actualizarSexo(valor: String) {
        _sexo.value = valor
    }

    fun actualizarFechaNacimiento(valor: String) {
        _fechaNacimiento.value = valor
    }

    fun actualizarGradoEscolaridad(valor: String) {
        _gradoEscolaridad.value = valor
    }

    fun datosValidos(): Boolean {
        val nombresValido = nombres.value.trim().isNotEmpty()
        val apellidosValido = apellidos.value.trim().isNotEmpty()
        val fechaValida = fechaNacimiento.value.isNotEmpty()

        return nombresValido && apellidosValido && fechaValida
    }

    fun obtenerDatosValidados(): PersonalData {
        return PersonalData(
            nombres = nombres.value.trim(),
            apellidos = apellidos.value.trim(),
            sexo = sexo.value,
            fechaNacimiento = fechaNacimiento.value,
            gradoEscolaridad = gradoEscolaridad.value
        )
    }
}
