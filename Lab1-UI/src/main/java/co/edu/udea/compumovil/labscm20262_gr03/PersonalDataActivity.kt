package co.edu.udea.compumovil.labscm20262_gr03

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
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.labscm20262_gr03.R
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
        stringResource(R.string.primary),
        stringResource(R.string.secondary),
        stringResource(R.string.university),
        stringResource(R.string.other)
    )

    val maleText = stringResource(R.string.male)
    val femaleText = stringResource(R.string.female)

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
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDatePicker = false }
                ) {
                    Text(stringResource(R.string.cancel))
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
                Text(text = stringResource(R.string.personal_info))

                OutlinedTextField(
                    value = nombres,
                    onValueChange = { 
                        val capitalizado = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        viewModel.actualizarNombres(capitalizado)
                    },
                    label = { Text(stringResource(R.string.names)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words,
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
                            Text(stringResource(R.string.required_field))
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
                    label = { Text(stringResource(R.string.surnames)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words,
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
                            Text(stringResource(R.string.required_field))
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
                Text(text = stringResource(R.string.sex))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexo == maleText,
                            onClick = {
                                viewModel.actualizarSexo(maleText)
                                fechaFocus.requestFocus()
                            },
                            modifier = Modifier.focusRequester(sexoFocus)
                        )
                        Text(
                            text = maleText,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexo == femaleText,
                            onClick = {
                                viewModel.actualizarSexo(femaleText)
                                fechaFocus.requestFocus()
                            }
                        )
                        Text(
                            text = femaleText,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Column {
                    Text(stringResource(R.string.birth_date))
                    if (mostrarErrores && fechaNacimiento.isEmpty()) {
                        Text(stringResource(R.string.required_field), color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                    }
                }
                Button(
                    onClick = { mostrarDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(fechaFocus)
                ) {
                    Text(if (fechaNacimiento.isEmpty()) stringResource(R.string.select_date) else fechaNacimiento)
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
                        label = { Text(stringResource(R.string.education_level)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = gradoExpandido
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            autoCorrectEnabled = false
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
                    onClick = {
                        mostrarErrores = !viewModel.datosValidos()
                        if (!mostrarErrores) {
                            val datos = viewModel.obtenerDatosValidados()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.continue_btn))
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
                text = stringResource(R.string.personal_info)
            )

            OutlinedTextField(
                value = nombres,
                onValueChange = { 
                    val capitalizado = it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    viewModel.actualizarNombres(capitalizado)
                },
                label = { Text(stringResource(R.string.names)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
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
                        Text(stringResource(R.string.required_field))
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
                label = { Text(stringResource(R.string.surnames)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
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
                        Text(stringResource(R.string.required_field))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(apellidosFocus)
            )

            Text(text = stringResource(R.string.sex))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = sexo == maleText,
                        onClick = {
                            viewModel.actualizarSexo(maleText)
                            fechaFocus.requestFocus()
                        },
                        modifier = Modifier.focusRequester(sexoFocus)
                    )
                    Text(
                        text = maleText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = sexo == femaleText,
                        onClick = {
                            viewModel.actualizarSexo(femaleText)
                            fechaFocus.requestFocus()
                        }
                    )
                    Text(
                        text = femaleText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column {
                Text(stringResource(R.string.birth_date))
                if (mostrarErrores && fechaNacimiento.isEmpty()) {
                    Text(stringResource(R.string.required_field), color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            }
            Button(
                onClick = { mostrarDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(fechaFocus)
            ) {
                Text(if (fechaNacimiento.isEmpty()) stringResource(R.string.select_date) else fechaNacimiento)
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
                    label = { Text(stringResource(R.string.education_level)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = gradoExpandido
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
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
                onClick = {
                    mostrarErrores = !viewModel.datosValidos()
                    if (!mostrarErrores) {
                        val datos = viewModel.obtenerDatosValidados()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.continue_btn))
            }
        }
    }
}
