package co.edu.udea.compumovil.labscm20262_gr03.ui.contact

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.labscm20262_gr03.MainActivity
import co.edu.udea.compumovil.labscm20262_gr03.R
import co.edu.udea.compumovil.labscm20262_gr03.navigation.DatosPersonalesRecibidos
import co.edu.udea.compumovil.labscm20262_gr03.navigation.obtenerDatosPersonales
import co.edu.udea.compumovil.labscm20262_gr03.ui.theme.LabsCM20262Gr03Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactDataActivity : ComponentActivity() {

    private val viewModel: ContactDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val datosPersonales = intent.obtenerDatosPersonales()

        setContent {
            ContactDataScreen(viewModel, datosPersonales)
        }
    }
}

private const val TAG_LOG = "DatosUsuario"

private const val MAX_TELEFONO = 15
private const val MAX_EMAIL = 100
private const val MAX_PAIS = 60
private const val MAX_CIUDAD = 60
private const val MAX_DIRECCION = 120

private fun registrarDatosEnLogcat(
    personales: DatosPersonalesRecibidos,
    telefono: String,
    direccion: String,
    email: String,
    pais: String,
    ciudad: String
) {
    Log.d(TAG_LOG, "Información personal:")
    Log.d(TAG_LOG, "${personales.nombres} ${personales.apellidos}")
    if (personales.sexo.isNotBlank()) {
        Log.d(TAG_LOG, personales.sexo)
    }
    Log.d(TAG_LOG, "Nació el ${personales.fechaNacimiento}")
    if (personales.gradoEscolaridad.isNotBlank()) {
        Log.d(TAG_LOG, personales.gradoEscolaridad)
    }

    Log.d(TAG_LOG, "Información de contacto:")
    Log.d(TAG_LOG, "Teléfono: $telefono")
    if (direccion.isNotBlank()) {
        Log.d(TAG_LOG, "Dirección: $direccion")
    }
    Log.d(TAG_LOG, "Email: $email")
    Log.d(TAG_LOG, "País: $pais")
    if (ciudad.isNotBlank()) {
        Log.d(TAG_LOG, "Ciudad: $ciudad")
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactDataScreen(
    viewModel: ContactDataViewModel,
    datosPersonales: DatosPersonalesRecibidos = DatosPersonalesRecibidos()
) {

    val paisesLatinoamerica = stringArrayResource(R.array.paises_latinoamerica).toList()
    val ciudadesColombia = stringArrayResource(R.array.ciudades_colombia).toList()

    val telefono by viewModel.telefono.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val email by viewModel.email.collectAsState()
    val pais by viewModel.pais.collectAsState()
    val ciudad by viewModel.ciudad.collectAsState()
    var mostrarErrores by remember { mutableStateOf(false) }
    var paisExpandido by remember { mutableStateOf(false) }
    var ciudadExpandida by remember { mutableStateOf(false) }

    val telefonoFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val paisFocus = remember { FocusRequester() }
    val ciudadFocus = remember { FocusRequester() }
    val direccionFocus = remember { FocusRequester() }

    val telefonoBringIntoView = remember { BringIntoViewRequester() }
    val emailBringIntoView = remember { BringIntoViewRequester() }
    val paisBringIntoView = remember { BringIntoViewRequester() }
    val ciudadBringIntoView = remember { BringIntoViewRequester() }
    val direccionBringIntoView = remember { BringIntoViewRequester() }

    val scope = rememberCoroutineScope()

    val teclado = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val campoTelefono: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.contacto_label_telefono),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    if (it.length <= MAX_TELEFONO) viewModel.actualizarTelefono(it)
                },
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { emailFocus.requestFocus() }
                ),
                isError = mostrarErrores && telefono.trim().length < 7,
                supportingText = {
                    if (mostrarErrores && telefono.trim().length < 7) {
                        Text(stringResource(R.string.contacto_error_telefono))
                    } else {
                        Text("${telefono.length}/$MAX_TELEFONO")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(telefonoFocus)
                    .bringIntoViewRequester(telefonoBringIntoView)
                    .onFocusChanged {
                        if (it.isFocused) {
                            teclado?.show()
                            scope.launch {
                                delay(200)
                                telefonoBringIntoView.bringIntoView()
                            }
                        }
                    },
            )
        }
    }

    val campoEmail: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.contacto_label_email),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (it.length <= MAX_EMAIL) viewModel.actualizarEmail(it)
                },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { paisFocus.requestFocus() }
                ),
                isError = mostrarErrores &&
                        !Patterns.EMAIL_ADDRESS
                            .matcher(email.trim())
                            .matches(),
                supportingText = {
                    if (mostrarErrores &&
                        !Patterns.EMAIL_ADDRESS
                            .matcher(email.trim())
                            .matches()
                    ) {
                        Text(stringResource(R.string.contacto_error_email))
                    } else {
                        Text("${email.length}/$MAX_EMAIL")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocus)
                    .bringIntoViewRequester(emailBringIntoView)
                    .onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                delay(200)
                                emailBringIntoView.bringIntoView()
                            }
                        }
                    }
            )
        }
    }

    val campoPais: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.contacto_label_pais),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            ExposedDropdownMenuBox(
                expanded = paisExpandido,
                onExpandedChange = { paisExpandido = !paisExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = pais,
                    onValueChange = {
                        if (it.length <= MAX_PAIS) {
                            viewModel.actualizarPais(it)
                            paisExpandido = true
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = paisExpandido)
                    },
                    isError = mostrarErrores && pais.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { ciudadFocus.requestFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .focusRequester(paisFocus)
                        .bringIntoViewRequester(paisBringIntoView)
                        .onFocusChanged {
                            if (it.isFocused) {
                                scope.launch {
                                    delay(200)
                                    paisBringIntoView.bringIntoView()
                                }
                            }
                        }
                )

                ExposedDropdownMenu(
                    expanded = paisExpandido,
                    onDismissRequest = { paisExpandido = false }
                ) {
                    paisesLatinoamerica
                        .filter { it.contains(pais, ignoreCase = true) }
                        .forEach { paisSeleccionado ->
                            DropdownMenuItem(
                                text = { Text(paisSeleccionado) },
                                onClick = {
                                    viewModel.actualizarPais(paisSeleccionado)
                                    paisExpandido = false
                                    ciudadFocus.requestFocus()
                                }
                            )
                        }
                }
            }
        }
    }

    val campoCiudad: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.contacto_label_ciudad),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            ExposedDropdownMenuBox(
                expanded = ciudadExpandida,
                onExpandedChange = { ciudadExpandida = !ciudadExpandida },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = {
                        if (it.length <= MAX_CIUDAD) {
                            viewModel.actualizarCiudad(it)
                            ciudadExpandida = true
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = ciudadExpandida)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { direccionFocus.requestFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .focusRequester(ciudadFocus)
                        .bringIntoViewRequester(ciudadBringIntoView)
                        .onFocusChanged {
                            if (it.isFocused) {
                                scope.launch {
                                    delay(200)
                                    ciudadBringIntoView.bringIntoView()
                                }
                            }
                        }
                )

                ExposedDropdownMenu(
                    expanded = ciudadExpandida,
                    onDismissRequest = { ciudadExpandida = false }
                ) {
                    ciudadesColombia
                        .filter { it.contains(ciudad, ignoreCase = true) }
                        .forEach { ciudadSeleccionada ->
                            DropdownMenuItem(
                                text = { Text(ciudadSeleccionada) },
                                onClick = {
                                    viewModel.actualizarCiudad(ciudadSeleccionada)
                                    ciudadExpandida = false
                                    direccionFocus.requestFocus()
                                }
                            )
                        }
                }
            }
        }
    }

    val campoDireccion: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.contacto_label_direccion),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = direccion,
                onValueChange = {
                    if (it.length <= MAX_DIRECCION) viewModel.actualizarDireccion(it)
                },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, // Corregido: antes tenía KeyboardType.Password
                    imeAction = ImeAction.Done
                ),
                supportingText = { Text("${direccion.length}/$MAX_DIRECCION") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(direccionFocus)
                    .bringIntoViewRequester(direccionBringIntoView)
                    .onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                delay(200)
                                direccionBringIntoView.bringIntoView()
                            }
                        }
                    }
            )
        }
    }

    val botonSiguiente: @Composable (Modifier) -> Unit = { modifier ->
        Button(
            onClick = {
                val contactoValido = viewModel.datosValidos()
                mostrarErrores = !contactoValido

                if (contactoValido && datosPersonales.sonValidos) {
                    registrarDatosEnLogcat(
                        personales = datosPersonales,
                        telefono = telefono,
                        direccion = direccion,
                        email = email,
                        pais = pais,
                        ciudad = ciudad
                    )
                    Toast.makeText(
                        context,
                        context.getString(R.string.contacto_mensaje_exito),
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    context.startActivity(intent)

                } else if (contactoValido && !datosPersonales.sonValidos) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.contacto_mensaje_datos_previos_faltantes),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = modifier
        ) {
            Text(stringResource(R.string.accion_siguiente))
        }
    }

    // Uso de LazyColumn para asegurar que el scroll automático funcione dinámicamente con el teclado

    if (isLandscape) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Text(text = stringResource(R.string.contacto_titulo_pantalla), fontWeight = FontWeight.Bold) }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    campoTelefono(Modifier.weight(1f))
                    campoEmail(Modifier.weight(1f))
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    campoPais(Modifier.weight(1f))
                    campoCiudad(Modifier.weight(1f))
                }
            }

            item { campoDireccion(Modifier.fillMaxWidth()) }
            item { botonSiguiente(Modifier.fillMaxWidth()) }
            item { Spacer(modifier = Modifier.height(200.dp)) }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Text(text = stringResource(R.string.contacto_titulo_pantalla), fontWeight = FontWeight.Bold) }

            item { campoTelefono(Modifier.fillMaxWidth()) }
            item { campoEmail(Modifier.fillMaxWidth()) }
            item { campoPais(Modifier.fillMaxWidth()) }
            item { campoCiudad(Modifier.fillMaxWidth()) }
            item { campoDireccion(Modifier.fillMaxWidth()) }
            item { botonSiguiente(Modifier.fillMaxWidth()) }
            item { Spacer(modifier = Modifier.height(200.dp)) }
        }
    }
}

@Preview(showBackground = true, name = "Contacto - Portrait")
@Composable
fun ContactDataScreenPreview() {
    LabsCM20262Gr03Theme {
        ContactDataScreen(viewModel = ContactDataViewModel())
    }
}

@Preview(
    showBackground = true,
    name = "Contacto - Landscape",
    widthDp = 640,
    heightDp = 360
)
@Composable
fun ContactDataScreenLandscapePreview() {
    LabsCM20262Gr03Theme {
        ContactDataScreen(viewModel = ContactDataViewModel())
    }
}