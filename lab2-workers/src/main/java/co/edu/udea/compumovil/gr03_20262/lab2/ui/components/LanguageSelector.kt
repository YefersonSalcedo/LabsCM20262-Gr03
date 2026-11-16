package co.edu.udea.compumovil.gr03_20262.lab2.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr03_20262.lab2.JetnewsApplication
import co.edu.udea.compumovil.gr03_20262.lab2.R
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.LanguageStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private data class LanguageOption(val tag: String?, val labelRes: Int)

private val DRAWER_OPTIONS = listOf(
    LanguageOption(null, R.string.language_system),
    LanguageOption("en", R.string.language_english),
    LanguageOption("es", R.string.language_spanish),
)

private val RAIL_OPTIONS = listOf(
    LanguageOption(null, R.string.language_auto),
    LanguageOption("en", R.string.language_english_short),
    LanguageOption("es", R.string.language_spanish_short),
)

private fun selectLanguage(context: Context, tag: String?) {
    LanguageStore.setLanguage(context, tag)
    // Recarga el feed con el nuevo idioma antes de recrear la actividad.
    // Se usa un scope de aplicación para que el refresh sobreviva al recreate.
    val app = context.applicationContext as? JetnewsApplication
    app?.let {
        CoroutineScope(Dispatchers.IO).launch {
            it.container.postsRepository.refreshFeed()
        }
    }
    (context as? Activity)?.recreate()
}

@Composable
fun LanguageDrawerSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val current = LanguageStore.getLanguage(context)

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider()
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
        )
        DRAWER_OPTIONS.forEach { option ->
            NavigationDrawerItem(
                label = { Text(stringResource(option.labelRes)) },
                icon = { Icon(painterResource(R.drawable.ic_language), null) },
                selected = current == option.tag,
                onClick = { selectLanguage(context, option.tag) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }
    }
}

@Composable
fun LanguageRailSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val current = LanguageStore.getLanguage(context)

    Column(modifier = modifier) {
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        RAIL_OPTIONS.forEach { option ->
            NavigationRailItem(
                selected = current == option.tag,
                onClick = { selectLanguage(context, option.tag) },
                icon = { Icon(painterResource(R.drawable.ic_language), null) },
                label = { Text(stringResource(option.labelRes)) },
                alwaysShowLabel = true,
            )
        }
    }
}
