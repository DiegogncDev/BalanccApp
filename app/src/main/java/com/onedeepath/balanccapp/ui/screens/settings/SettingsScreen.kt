package com.onedeepath.balanccapp.ui.screens.settings

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.data.datastore.SettingsPreferences
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsPreferences: SettingsPreferences,
    isDarkTheme: Boolean,
) {
    val scope = rememberCoroutineScope()
    val currentLangCode by settingsPreferences.languageCode.collectAsState(initial = "es")
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Mapeo simple para mostrar el nombre bonito en la UI
    val languageDisplay = if (currentLangCode == "es") "Español" else "English"

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Sección: Apariencia
                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary, // 0xFF8E44AD
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingItem(
                    title = stringResource(R.string.dark_mode),
                    description = stringResource(R.string.change_application_theme),
                    icon = Icons.Default.CheckCircle,
                    trailing = {
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { newValue ->
                                scope.launch { settingsPreferences.setDarkMode(newValue) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Sección: Preferencias
                Text(
                    text = stringResource(R.string.preferences),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SettingItem(
                    title = stringResource(R.string.language),
                    description = stringResource(R.string.select_language),
                    icon = Icons.Default.AddCircle,
                    onClick = {
                        showLanguageDialog = true
                    },
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = languageDisplay,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline // 0xFF7F747C
                            )
                            Icon(
                                Icons.Default.Create,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                )


                if (showLanguageDialog) {
                    LanguagePickerDialog(
                        currentLanguageCode = currentLangCode,
                        onLanguageSelected = { langCode ->
                            scope.launch {
                                settingsPreferences.setLanguage(langCode)
                                // Aplicar el cambio a nivel sistema para la App
                                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            }
                            showLanguageDialog = false
                        },
                        onDismiss = { showLanguageDialog = false }
                    )
                }

            }
        }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono con fondo suave (usando tus colores de contenedor)
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer, // 0xFFF3E5F5
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        trailing()
    }
}

@Composable
fun LanguagePickerDialog(
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf("Español" to "es", "English" to "en")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text(stringResource(R.string.select_language), fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn {
                items(languages) { (name, code) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, style = MaterialTheme.typography.bodyLarge)
                        if (code == currentLanguageCode) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF8E44AD) // Tu PrimaryLight
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}