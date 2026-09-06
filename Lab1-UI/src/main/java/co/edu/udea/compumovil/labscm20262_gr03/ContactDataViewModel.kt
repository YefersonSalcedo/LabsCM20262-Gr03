package co.edu.udea.compumovil.labscm20262_gr03

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactDataViewModel : ViewModel() {

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _pais = MutableStateFlow("")
    val pais: StateFlow<String> = _pais.asStateFlow()

    private val _ciudad = MutableStateFlow("")
    val ciudad: StateFlow<String> = _ciudad.asStateFlow()

    fun actualizarTelefono(valor: String) {
        _telefono.value = valor
    }

    fun actualizarDireccion(valor: String) {
        _direccion.value = valor
    }

    fun actualizarEmail(valor: String) {
        _email.value = valor
    }

    fun actualizarPais(valor: String) {
        _pais.value = valor
    }

    fun actualizarCiudad(valor: String) {
        _ciudad.value = valor
    }

    fun datosValidos(): Boolean {
        val telefonoValido = telefono.value.trim().length >= 7

        val emailValido = android.util.Patterns.EMAIL_ADDRESS
            .matcher(email.value.trim())
            .matches()

        val paisValido = pais.value.isNotBlank()


        return telefonoValido &&
                emailValido &&
                paisValido

    }
}