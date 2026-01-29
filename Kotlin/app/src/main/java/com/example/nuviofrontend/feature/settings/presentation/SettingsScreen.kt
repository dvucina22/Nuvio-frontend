package com.example.nuviofrontend.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.R
import com.example.core.settings.CurrencyPreference
import com.example.core.settings.LanguagePreference
import com.example.core.settings.LocaleManager
import com.example.core.settings.localizedString
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.WhiteSoft
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedCurrency by viewModel.currencyFlow.collectAsState(initial = 1)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val language by viewModel.languageFlow.collectAsState(initial = 0)

    val selectedAppearance by viewModel.themeFlow.collectAsState(initial = 0)

    Column(modifier = Modifier.fillMaxSize()) {

        CustomTopBar(
            title = localizedString(resId = R.string.settings_title),
            showBack = true,
            onBack = onBack
        )

        Column(modifier = Modifier.padding(20.dp)) {

            SectionTitle(text = localizedString(resId = R.string.general))

            Spacer(modifier = Modifier.height(8.dp))

            Container {
                SettingsRow(
                    title = localizedString(resId = R.string.language),
                    options = listOf("HR", "EN"),
                    selectedIndex = language,
                    onOptionSelected = { index ->
                        scope.launch {
                            val languageCode = if (index == 0) "hr" else "en"
                            viewModel.saveLanguage(index)
                            LanguagePreference.saveLanguage(context, languageCode)
                            LocaleManager.setLocale(context, Locale(languageCode))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Container {
                SettingsRow(
                    title = localizedString(resId = R.string.currency),
                    options = listOf("$", "€"),
                    selectedIndex = selectedCurrency,
                    onOptionSelected = { index ->
                        scope.launch {
                            val selectedCurrencySymbol = if (index == 0) "$" else "€"
                            viewModel.saveCurrency(index)
                            CurrencyPreference.saveCurrency(context, selectedCurrencySymbol)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(text = localizedString(resId = R.string.display))

            Spacer(modifier = Modifier.height(8.dp))

            Container {
                SettingsRow(
                    title = localizedString(resId =R.string.appearance),
                    options = listOf(
                        localizedString(resId =R.string.light),
                        localizedString(resId =R.string.dark)
                    ),
                    selectedIndex = selectedAppearance,
                    onOptionSelected = {
                        scope.launch {
                            viewModel.saveTheme(it)
                        }
                    }

                )
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayLarge
    )
}

@Composable
fun SettingsRow(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground
        )

        SegmentedControl(
            options = options,
            selectedIndex = selectedIndex,
            onOptionSelected = onOptionSelected
        )
    }
}

@Composable
fun Container(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            content = content
        )
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(2.dp)
    ) {
        options.forEachIndexed { index, option ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (index == selectedIndex) AccentColor else MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                    .clickable { onOptionSelected(index) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (index == selectedIndex) WhiteSoft else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}