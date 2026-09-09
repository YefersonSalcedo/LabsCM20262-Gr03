package co.edu.udea.compumovil.labscm20262_gr03

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.labscm20262_gr03.ui.contact.ContactDataActivity
import co.edu.udea.compumovil.labscm20262_gr03.ui.personal.PersonalDataActivity
import co.edu.udea.compumovil.labscm20262_gr03.ui.theme.LabsCM20262Gr03Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LabsCM20262Gr03Theme {
                MainScreen(
                    onPersonalDataClick = {
                        val intent = Intent(
                            this,
                            PersonalDataActivity::class.java
                        )
                        startActivity(intent)
                    },
                    onContactDataClick = {
                        val intent = Intent(
                            this,
                            ContactDataActivity::class.java
                        )
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    onPersonalDataClick: () -> Unit,
    onContactDataClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Laboratorio CM"
        )

        Button(
            onClick = onPersonalDataClick
        ) {
            Text("Información personal")
        }

        Button(
            onClick = onContactDataClick
        ) {
            Text("Información de contacto")
        }
    }
}

