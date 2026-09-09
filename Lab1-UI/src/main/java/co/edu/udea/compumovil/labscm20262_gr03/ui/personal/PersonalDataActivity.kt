package co.edu.udea.compumovil.labscm20262_gr03.ui.personal

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.labscm20262_gr03.navigation.DatosPersonalesExtras
import co.edu.udea.compumovil.labscm20262_gr03.ui.contact.ContactDataActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PersonalDataActivity : ComponentActivity() {

    private val viewModel: PersonalDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PersonalDataScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(viewModel: PersonalDataViewModel) {

    val gradosEscolaridad = listOf(
        "Primaria",
        "Secundaria",
        "Universitaria",
        "Otro"
    )

    val nombres by viewModel.nombres.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val sexo by viewModel.sexo.collectAsState()
    val fechaNacimiento by viewModel.fechaNacimiento.collectAsState()
    val gradoEscolaridad by viewModel.gradoEscolaridad.collectAsState()

    var mostrarErrores by remember { mutableStateOf(false) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var gradoExpandido by remember { mutableStateOf(false) }

    val nombresFocus = remember { FocusRequester() }
    val apellidosFocus = remember { FocusRequester() }
    val sexoFocus = remember { FocusRequester() }
    val fechaFocus = remember { FocusRequester() }
    val gradoFocus = remember { FocusRequester() }

    val teclado = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current

    // Integrante 3 - Navegación: al validar los datos obligatorios de esta
    // pantalla, se envían como Intent extras hacia ContactDataActivity.
    val irAContacto: () -> Unit = {
        mostrarErrores = !viewModel.datosValidos()
        if (!mostrarErrores) {
            val datos = viewModel.obtenerDatosValidados()
            val intent = Intent(context, ContactDataActivity::class.java).apply {
                putExtra(DatosPersonalesExtras.NOMBRES, datos.nombres)
                putExtra(DatosPersonalesExtras.APELLIDOS, datos.apellidos)
                putExtra(DatosPersonalesExtras.SEXO, datos.sexo)
                putExtra(DatosPersonalesExtras.FECHA_NACIMIENTO, datos.fechaNacimiento)
                putExtra(DatosPersonalesExtras.GRADO_ESCOLARIDAD, datos.gradoEscolaridad)
            }
            context.startActivity(intent)
        }
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            viewModel.actualizarFechaNacimiento(formatter.format(Date(millis)))
                        }
                        mostrarDatePicker = false
                        gradoFocus.requestFocus()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDatePicker = false }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Información Personal")

                OutlinedTextField(
                    value = nombres,
                    onValueChange = {
                        val capitalizado = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        viewModel.actualizarNombres(capitalizado)
                    },
                    label = { Text("Nombres") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            apellidosFocus.requestFocus()
                        }
                    ),
                    isError = mostrarErrores && nombres.trim().isEmpty(),
                    supportingText = {
                        if (mostrarErrores && nombres.trim().isEmpty()) {
                            Text("Campo obligatorio")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(nombresFocus)
                )

                OutlinedTextField(
                    value = apellidos,
                    onValueChange = {
                        val capitalizado = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        viewModel.actualizarApellidos(capitalizado)
                    },
                    label = { Text("Apellidos") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            sexoFocus.requestFocus()
                        }
                    ),
                    isError = mostrarErrores && apellidos.trim().isEmpty(),
                    supportingText = {
                        if (mostrarErrores && apellidos.trim().isEmpty()) {
                            Text("Campo obligatorio")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(apellidosFocus)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Sexo")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexo == "Masculino",
                            onClick = {
                                viewModel.actualizarSexo("Masculino")
                                fechaFocus.requestFocus()
                            },
                            modifier = Modifier.focusRequester(sexoFocus)
                        )
                        Text(
                            text = "Masculino",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexo == "Femenino",
                            onClick = {
                                viewModel.actualizarSexo("Femenino")
                                fechaFocus.requestFocus()
                            }
                        )
                        Text(
                            text = "Femenino",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Column {
                    Text("Fecha de nacimiento")
                    if (mostrarErrores && fechaNacimiento.isEmpty()) {
                        Text("Campo obligatorio", color = MaterialTheme.colorScheme.error)
                    }
                }
                Button(
                    onClick = { mostrarDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(fechaFocus)
                ) {
                    Text(if (fechaNacimiento.isEmpty()) "Cambiar" else fechaNacimiento)
                }

                ExposedDropdownMenuBox(
                    expanded = gradoExpandido,
                    onExpandedChange = {
                        gradoExpandido = !gradoExpandido
                    }
                ) {
                    OutlinedTextField(
                        value = gradoEscolaridad,
                        onValueChange = {
                            viewModel.actualizarGradoEscolaridad(it)
                            gradoExpandido = true
                        },
                        label = { Text("Grado de escolaridad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = gradoExpandido
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .focusRequester(gradoFocus)
                    )

                    ExposedDropdownMenu(
                        expanded = gradoExpandido,
                        onDismissRequest = {
                            gradoExpandido = false
                        }
                    ) {
                        gradosEscolaridad.forEach { grado ->
                            DropdownMenuItem(
                                text = {
                                    Text(grado)
                                },
                                onClick = {
                                    viewModel.actualizarGradoEscolaridad(grado)
                                    gradoExpandido = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = irAContacto,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información Personal"
            )

            OutlinedTextField(
                value = nombres,
                onValueChange = {
                    val capitalizado = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    viewModel.actualizarNombres(capitalizado)
                },
                label = { Text("Nombres") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = false
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        apellidosFocus.requestFocus()
                    }
                ),
                isError = mostrarErrores && nombres.trim().isEmpty(),
                supportingText = {
                    if (mostrarErrores && nombres.trim().isEmpty()) {
                        Text("Campo obligatorio")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nombresFocus)
            )

            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    val capitalizado = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    viewModel.actualizarApellidos(capitalizado)
                },
                label = { Text("Apellidos") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = false
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        sexoFocus.requestFocus()
                    }
                ),
                isError = mostrarErrores && apellidos.trim().isEmpty(),
                supportingText = {
                    if (mostrarErrores && apellidos.trim().isEmpty()) {
                        Text("Campo obligatorio")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(apellidosFocus)
            )

            Text(text = "Sexo")

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = sexo == "Masculino",
                        onClick = {
                            viewModel.actualizarSexo("Masculino")
                            fechaFocus.requestFocus()
                        },
                        modifier = Modifier.focusRequester(sexoFocus)
                    )
                    Text(
                        text = "Masculino",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = sexo == "Femenino",
                        onClick = {
                            viewModel.actualizarSexo("Femenino")
                            fechaFocus.requestFocus()
                        }
                    )
                    Text(
                        text = "Femenino",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column {
                Text("Fecha de nacimiento")
                if (mostrarErrores && fechaNacimiento.isEmpty()) {
                    Text("Campo obligatorio", color = MaterialTheme.colorScheme.error)
                }
            }
            Button(
                onClick = { mostrarDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(fechaFocus)
            ) {
                Text(if (fechaNacimiento.isEmpty()) "Cambiar" else fechaNacimiento)
            }

            ExposedDropdownMenuBox(
                expanded = gradoExpandido,
                onExpandedChange = {
                    gradoExpandido = !gradoExpandido
                }
            ) {
                OutlinedTextField(
                    value = gradoEscolaridad,
                    onValueChange = {
                        viewModel.actualizarGradoEscolaridad(it)
                        gradoExpandido = true
                    },
                    label = { Text("Grado de escolaridad") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = gradoExpandido
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .focusRequester(gradoFocus)
                )

                ExposedDropdownMenu(
                    expanded = gradoExpandido,
                    onDismissRequest = {
                        gradoExpandido = false
                    }
                ) {
                    gradosEscolaridad.forEach { grado ->
                        DropdownMenuItem(
                            text = {
                                Text(grado)
                            },
                            onClick = {
                                viewModel.actualizarGradoEscolaridad(grado)
                                gradoExpandido = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = irAContacto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}
