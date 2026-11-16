package co.edu.udea.compumovil.gr03_20262.lab2.ui.home

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.work.WorkInfo
import androidx.work.WorkManager
import co.edu.udea.compumovil.gr03_20262.lab2.R
import co.edu.udea.compumovil.gr03_20262.lab2.work.SyncScheduler
import java.util.UUID
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
fun SyncNowButton(
    onSyncCompleted: () -> Unit,
) {
    val context = LocalContext.current
    val appContext = context.applicationContext

    val workManager = remember(appContext) {
        WorkManager.getInstance(appContext)
    }

    var requestId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val currentOnSyncCompleted by rememberUpdatedState(onSyncCompleted)

    LaunchedEffect(requestId) {
        val id = requestId ?: return@LaunchedEffect

        val workInfo = workManager
            .getWorkInfoByIdFlow(UUID.fromString(id))
            .filterNotNull()
            .first { it.state.isFinished }

        requestId = null

        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
                currentOnSyncCompleted()

                Toast.makeText(
                    context,
                    R.string.sync_completed,
                    Toast.LENGTH_SHORT,
                ).show()
            }

            WorkInfo.State.FAILED -> {
                Toast.makeText(
                    context,
                    R.string.sync_failed,
                    Toast.LENGTH_SHORT,
                ).show()
            }

            else -> Unit
        }
    }

    val syncText = stringResource(
        if (requestId == null) {
            R.string.sync_now
        } else {
            R.string.sync_in_progress
        },
    )

    TextButton(
        enabled = requestId == null,
        onClick = {
            if (!isNetworkConnected(context)) {
                Toast.makeText(
                    context,
                    R.string.sync_failed,
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                requestId = SyncScheduler
                    .syncNow(appContext)
                    .toString()

                Toast.makeText(
                    context,
                    R.string.sync_started,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        },
        modifier = Modifier.semantics {
            contentDescription = syncText
        },
    ) {
        Text(
            text = syncText,
        )
    }
}

private fun isNetworkConnected(context: android.content.Context): Boolean {
    val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
}