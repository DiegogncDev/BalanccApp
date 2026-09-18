package com.onedeepath.balanccapp.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.data.datastore.SettingsPreferences
import com.onedeepath.balanccapp.ui.components.BalanccTopBar
import com.onedeepath.balanccapp.ui.components.SectionHeader
import com.onedeepath.balanccapp.ui.components.SelectionDialog
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsPreferences: SettingsPreferences,
    isDarkTheme: Boolean,
) {
    val scope = rememberCoroutineScope()
    val currentLangCode by settingsPreferences.languageCode.collectAsState(initial = "es")
    var showLanguageDialog by remember { mutableStateOf(false) }

    val languageDisplay = if (currentLangCode == "es") "Español" else "English"
    val languageOptions = listOf("Español" to "es", "English" to "en")

    Scaffold(
        topBar = {
            BalanccTopBar(
                title = stringResource(R.string.settings),
                onNavigateBack = { navController.popBackStack() },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = BalanccSpacing.standard),
        ) {
            Spacer(modifier = Modifier.height(BalanccSpacing.small))

            SectionHeader(
                title = stringResource(R.string.appearance),
                modifier = Modifier.padding(
                    start = BalanccSpacing.micro,
                    top = BalanccSpacing.small,
                    bottom = BalanccSpacing.compact,
                ),
            )

            SettingItem(
                title = stringResource(R.string.dark_mode),
                description = stringResource(R.string.change_application_theme),
                icon = Icons.Default.Settings,
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { newValue ->
                            scope.launch { settingsPreferences.setDarkMode(newValue) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.financialColors.textTertiary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    )
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = BalanccSpacing.section),
                thickness = 1.dp,
                color = MaterialTheme.financialColors.border,
            )

            SectionHeader(
                title = stringResource(R.string.preferences),
                modifier = Modifier.padding(
                    start = BalanccSpacing.micro,
                    bottom = BalanccSpacing.compact,
                ),
            )

            SettingItem(
                title = stringResource(R.string.language),
                description = stringResource(R.string.select_language),
                icon = Icons.Default.Build,
                onClick = { showLanguageDialog = true },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = languageDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.financialColors.textSecondary,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(BalanccSpacing.micro))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.financialColors.textSecondary,
                        )
                    }
                },
            )

            if (showLanguageDialog) {
                SelectionDialog(
                    title = stringResource(R.string.select_language),
                    options = languageOptions,
                    optionLabel = { it.first },
                    onOptionSelected = { (_, code) ->
                        scope.launch {
                            settingsPreferences.setLanguage(code)
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(code)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                        }
                        showLanguageDialog = false
                    },
                    onDismiss = { showLanguageDialog = false },
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
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(BalanccCornerRadius.input),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = BalanccSpacing.standard,
                vertical = BalanccSpacing.compact,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(BalanccCornerRadius.control),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.width(BalanccSpacing.compact))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.financialColors.textSecondary,
                )
            }

            Spacer(Modifier.width(BalanccSpacing.small))

            trailing()
        }
    }
}
