/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.edu.udea.compumovil.gr03_20262.lab2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.gr03_20262.lab2.JetnewsApplication
import co.edu.udea.compumovil.gr03_20262.lab2.deeplink.handleDeepLink
import co.edu.udea.compumovil.gr03_20262.lab2.ui.home.HomeKey
import co.edu.udea.compumovil.gr03_20262.lab2.ui.navigation.rememberInitialBackStack
import co.edu.udea.compumovil.gr03_20262.lab2.ui.utils.NewIntentEffect

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    private val preferences by lazy {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        requestNotificationPermissionOnce()

        val appContainer = (application as JetnewsApplication).container
        setContent {
            val initialDeepLinkBackStack = remember { intent.data?.handleDeepLink() }
            var isOpenedByDeepLink by rememberSaveable { mutableStateOf(initialDeepLinkBackStack != null) }
            var initialBackStack by rememberInitialBackStack(initialDeepLinkBackStack ?: listOf(HomeKey))

            NewIntentEffect { newIntent ->
                newIntent.data?.handleDeepLink()?.let { deepLinkBackStack ->
                    isOpenedByDeepLink = true
                    initialBackStack = deepLinkBackStack
                }
            }

            JetnewsApp(
                appContainer,
                isBackEnabled = !isOpenedByDeepLink || isTaskRoot,
                initialBackStack,
            )
        }
    }

    private fun requestNotificationPermissionOnce() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (preferences.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false)) return

        preferences.edit()
            .putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true)
            .apply()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
    }
}
