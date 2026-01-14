package com.example.nuviofrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.theme.NuvioFrontendTheme
import com.example.nuviofrontend.navigation.NavigationHost
import dagger.hilt.android.AndroidEntryPoint
import com.example.core.R
import com.example.core.settings.LanguagePreference
import com.example.core.settings.LocaleManager
import com.example.core.ui.components.CustomButton
import java.util.Locale
import com.example.nuviofrontend.feature.settings.presentation.SettingsViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val savedLanguageCode = LanguagePreference.getSavedLanguage(this)
        LocaleManager.setLocale(this, Locale(savedLanguageCode))

        setContent {
            val themeIndex by settingsViewModel.themeFlow.collectAsState(initial = 0)

            NuvioFrontendTheme(darkTheme = themeIndex == 1) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationHost()
                }
            }
        }
    }
}


@Composable
fun MainScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onContinueAsGuest: () -> Unit,
    themeIndex: Int
) {
    val backgroundRes = if (themeIndex == 1) R.drawable.background_dark else R.drawable.background_light
    val logoRes = if (themeIndex == 1) R.drawable.logo_dark_full else R.drawable.logo_light_full
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "logo_dark_full",
                modifier = Modifier.size(230.dp),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CustomButton(
                    text = stringResource(R.string.login_title),
                    onClick = onNavigateToLogin
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomButton(
                    text = stringResource(R.string.registration_title),
                    onClick = onNavigateToRegister
                )
                Text(
                    text = stringResource(R.string.text_guest),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .clickable { onContinueAsGuest() }
                )
            }
        }
    }
}

