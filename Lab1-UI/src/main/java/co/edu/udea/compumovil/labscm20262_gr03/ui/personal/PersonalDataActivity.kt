package co.edu.udea.compumovil.labscm20262_gr03.ui.personal

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.labscm20262_gr03.R
import co.edu.udea.compumovil.labscm20262_gr03.navigation.DatosPersonalesExtras
import co.edu.udea.compumovil.labscm20262_gr03.ui.contact.ContactDataActivity
import co.edu.udea.compumovil.labscm20262_gr03.ui.theme.LabsCM20262Gr03Theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PersonalDataActivity : ComponentActivity() {

    private val viewModel: PersonalDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            PersonalDataScreen(viewModel)
        }
    }
}

private const val MAX_NOMBRES = 50
private const val MAX_APELLIDOS = 50

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PersonalDataScreen(viewModel: PersonalDataViewModel) {

    val gradosEscolaridad = stringArrayResource(R.array.grados_escolaridad).toList()

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

    val nombresBringIntoView = remember { BringIntoViewRequester() }
    val apellidosBringIntoView = remember { BringIntoViewRequester() }

    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val teclado = LocalSoftwareKeyboardController.current

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
                    Text(stringResource(R.string.accion_aceptar))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDatePicker = false }
                ) {
                    Text(stringResource(R.string.accion_cancelar))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val campoNombres: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.personal_label_nombres),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = nombres,
                onValueChange = {
                    if (it.length <= MAX_NOMBRES) {
                        val capitalizado = it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString() }
                        viewModel.actualizarNombres(capitalizado)
                    }
                },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(
                    onNext = { apellidosFocus.requestFocus() }
                ),
                isError = mostrarErrores && nombres.trim().isEmpty(),
                supportingText = {
                    if (mostrarErrores && nombres.trim().isEmpty()) {
                        Text(stringResource(R.string.error_campo_obligatorio))
                    } else {
                        Text("${nombres.length}/$MAX_NOMBRES")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nombresFocus)
                    .bringIntoViewRequester(nombresBringIntoView)
                    .onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                delay(200)
                                nombresBringIntoView.bringIntoView()
                            }
                        }
                    }
            )
        }
    }

    val campoApellidos: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.personal_label_apellidos),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    if (it.length <= MAX_APELLIDOS) {
                        val capitalizado = it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString() }
                        viewModel.actualizarApellidos(capitalizado)
                    }
                },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                ),
                keyboardActions = KeyboardActions(
                    // Al presionar Enter/Siguiente, se oculta el teclado y se enfoca en el botón de Fecha de Nacimiento
                    onNext = {
                        teclado?.hide()
                        fechaFocus.requestFocus()
                    }
                ),
                isError = mostrarErrores && apellidos.trim().isEmpty(),
                supportingText = {
                    if (mostrarErrores && apellidos.trim().isEmpty()) {
                        Text(stringResource(R.string.error_campo_obligatorio))
                    } else {
                        Text("${apellidos.length}/$MAX_APELLIDOS")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(apellidosFocus)
                    .bringIntoViewRequester(apellidosBringIntoView)
                    .onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                delay(200)
                                apellidosBringIntoView.bringIntoView()
                            }
                        }
                    }
            )
        }
    }

    val filaSexo: @Composable (Modifier) -> Unit = { modifier ->
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.personal_label_sexo), fontWeight = FontWeight.Medium)

            RadioButton(
                selected = sexo == "Masculino",
                onClick = {
                    viewModel.actualizarSexo("Masculino")
                    fechaFocus.requestFocus()
                },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .focusRequester(sexoFocus)
            )
            Text(text = stringResource(R.string.personal_sexo_hombre))

            RadioButton(
                selected = sexo == "Femenino",
                onClick = {
                    viewModel.actualizarSexo("Femenino")
                    fechaFocus.requestFocus()
                },
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(text = stringResource(R.string.personal_sexo_mujer))
        }
    }

    val filaFecha: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Text(
                        text = stringResource(R.string.personal_label_fecha_nacimiento),
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = { mostrarDatePicker = true },
                    modifier = Modifier.focusRequester(fechaFocus)
                ) {
                    Text(if (fechaNacimiento.isEmpty()) stringResource(R.string.accion_cambiar) else fechaNacimiento)
                }
            }

            if (mostrarErrores && fechaNacimiento.isEmpty()) {
                Text(
                    text = stringResource(R.string.error_campo_obligatorio),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    val campoGrado: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.personal_label_grado_escolaridad),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            ExposedDropdownMenuBox(
                expanded = gradoExpandido,
                onExpandedChange = { gradoExpandido = !gradoExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = gradoEscolaridad,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradoExpandido)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .focusRequester(gradoFocus)
                )

                ExposedDropdownMenu(
                    expanded = gradoExpandido,
                    onDismissRequest = { gradoExpandido = false }
                ) {
                    gradosEscolaridad.forEach { grado ->
                        DropdownMenuItem(
                            text = { Text(grado) },
                            onClick = {
                                viewModel.actualizarGradoEscolaridad(grado)
                                gradoExpandido = false
                            }
                        )
                    }
                }
            }
        }
    }

    val botonSiguiente: @Composable (Modifier) -> Unit = { modifier ->
        Button(
            onClick = irAContacto,
            modifier = modifier
        ) {
            Text(stringResource(R.string.accion_siguiente))
        }
    }

    // Distribución según orientación optimizada con LazyColumn

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
            item { Text(text = stringResource(R.string.personal_titulo_pantalla), fontWeight = FontWeight.Bold) }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    campoNombres(Modifier.weight(1f))
                    campoApellidos(Modifier.weight(1f))
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        filaSexo(Modifier)
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        filaFecha(Modifier)
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    campoGrado(Modifier.weight(1f))
                    botonSiguiente(Modifier.weight(1f))
                }
            }

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
            item { Text(text = stringResource(R.string.personal_titulo_pantalla), fontWeight = FontWeight.Bold) }

            item { campoNombres(Modifier.fillMaxWidth()) }
            item { campoApellidos(Modifier.fillMaxWidth()) }
            item { filaSexo(Modifier.fillMaxWidth()) }
            item { filaFecha(Modifier.fillMaxWidth()) }
            item { campoGrado(Modifier.fillMaxWidth()) }
            item { botonSiguiente(Modifier.fillMaxWidth()) }

            item { Spacer(modifier = Modifier.height(200.dp)) }
        }
    }
}

@Preview(showBackground = true, name = "Personal - Portrait")
@Composable
fun PersonalDataScreenPortraitPreview() {
    LabsCM20262Gr03Theme {
        PersonalDataScreen(viewModel = PersonalDataViewModel())
    }
}

@Preview(
    showBackground = true,
    name = "Personal - Landscape",
    widthDp = 640,
    heightDp = 360
)
@Composable
fun PersonalDataScreenLandscapePreview() {
    LabsCM20262Gr03Theme {
        PersonalDataScreen(viewModel = PersonalDataViewModel())
    }
}