package co.edu.udea.compumovil.labscm20262_gr03

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import co.edu.udea.compumovil.labscm20262_gr03.R


class ContactDataActivity : ComponentActivity() {

    private val viewModel: ContactDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ContactDataScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun ContactDataScreen(viewModel: ContactDataViewModel) {

    val paisesLatinoamerica = listOf(
        "Argentina",
        "Bolivia",
        "Brasil",
        "Chile",
        "Colombia",
        "Costa Rica",
        "Cuba",
        "Ecuador",
        "El Salvador",
        "Guatemala",
        "Honduras",
        "México",
        "Nicaragua",
        "Panamá",
        "Paraguay",
        "Perú",
        "República Dominicana",
        "Uruguay",
        "Venezuela"
    )

    val ciudadesColombia = listOf(
        "Bogotá",
        "Medellín",
        "Cali",
        "Barranquilla",
        "Cartagena",
        "Cúcuta",
        "Bucaramanga",
        "Pereira",
        "Santa Marta",
        "Ibagué",
        "Manizales",
        "Villavicencio",
        "Pasto",
        "Montería",
        "Neiva",
        "Armenia",
        "Valledupar",
        "Sincelejo",
        "Popayán",
        "Tunja"
    )

    val telefono by viewModel.telefono.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val email by viewModel.email.collectAsState()
    val pais by viewModel.pais.collectAsState()
    val ciudad by viewModel.ciudad.collectAsState()
    var mostrarErrores by remember { mutableStateOf(false) }
    var paisExpandido by remember { mutableStateOf(false) }
    var ciudadExpandida by remember { mutableStateOf(false) }

    val telefonoFocus = remember { FocusRequester() }
    val direccionFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val paisFocus = remember { FocusRequester() }
    val ciudadFocus = remember { FocusRequester() }

    val teclado = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(R.string.contact_info)
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { viewModel.actualizarTelefono(it) },
            label = { Text(stringResource(R.string.phone)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    direccionFocus.requestFocus()
                }
            ),
            isError = mostrarErrores && telefono.trim().length <7,
            supportingText = {
                if(mostrarErrores && telefono.trim().length < 7){
                    Text(stringResource(R.string.valid_phone))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(telefonoFocus)
                .onFocusChanged {
                    if (it.isFocused) {
                        teclado?.show()
                    }
                },
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { viewModel.actualizarDireccion(it) },
            label = { Text(stringResource(R.string.address)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                autoCorrectEnabled = false
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    emailFocus.requestFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(direccionFocus)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.actualizarEmail(it) },
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    paisFocus.requestFocus()
                }
            ),
            isError = mostrarErrores &&
                    !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim())
                        .matches(),
            supportingText = {
                if(mostrarErrores &&
                    !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email.trim())
                        .matches()
                    ){
                    Text(stringResource(R.string.valid_email))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocus)
        )

        ExposedDropdownMenuBox(
            expanded = paisExpandido,
            onExpandedChange = {
                paisExpandido = !paisExpandido
            }
        ) {

            OutlinedTextField(
                value = pais,
                onValueChange = {
                    viewModel.actualizarPais(it)
                    paisExpandido = true
                },
                label = { Text(stringResource(R.string.country)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = paisExpandido
                    )
                },
                isError = mostrarErrores && pais.isBlank(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        ciudadFocus.requestFocus()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .focusRequester(paisFocus)
            )

            ExposedDropdownMenu(
                expanded = paisExpandido,
                onDismissRequest = {
                    paisExpandido = false
                }
            ) {

                paisesLatinoamerica
                    .filter {
                        it.contains(
                            pais,
                            ignoreCase = true
                        )
                    }
                    .forEach { paisSeleccionado ->

                        DropdownMenuItem(
                            text = {
                                Text(paisSeleccionado)
                            },
                            onClick = {
                                viewModel.actualizarPais(
                                    paisSeleccionado
                                )
                                paisExpandido = false
                                ciudadFocus.requestFocus()
                            }
                        )
                    }
            }
        }

        ExposedDropdownMenuBox(
            expanded = ciudadExpandida,
            onExpandedChange = {
                ciudadExpandida = !ciudadExpandida
            }
        ) {

            OutlinedTextField(
                value = ciudad,
                onValueChange = {
                    viewModel.actualizarCiudad(it)
                    ciudadExpandida = true
                },
                label = { Text(stringResource(R.string.city)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = ciudadExpandida
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .focusRequester(ciudadFocus)
            )

            ExposedDropdownMenu(
                expanded = ciudadExpandida,
                onDismissRequest = {
                    ciudadExpandida = false
                }
            ) {

                ciudadesColombia
                    .filter {
                        it.contains(
                            ciudad,
                            ignoreCase = true
                        )
                    }
                    .forEach { ciudadSeleccionada ->

                        DropdownMenuItem(
                            text = {
                                Text(ciudadSeleccionada)
                            },
                            onClick = {
                                viewModel.actualizarCiudad(
                                    ciudadSeleccionada
                                )
                                ciudadExpandida = false
                            }
                        )
                    }
            }
        }

        Button(
            onClick = {
                mostrarErrores = !viewModel.datosValidos()
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(ciudadFocus)
        ) {
            Text(stringResource(R.string.continue_btn))
        }
    }
}

