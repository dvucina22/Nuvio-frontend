package com.example.nuviofrontend.feature.support.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.R
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.CardItemBackgroundLight

@Composable
fun SupportScreen(
    onBack: () -> Unit = {},
) {
    val faqQuestions = listOf(
        "Kako da naručim laptop?" to """
            1. Odaberi željeni laptop iz ponude
            2. Dodaj ga u košaricu
            3. Klikni na gumb plaćanje
            4. Unesi podatke
            5. Potvrdi narudžbu
        """.trimIndent(),
        "Koje su sve mogućnosti plaćanja?" to """
            Plaćanje je moguće na sljedeći način:
            Kreditnom ili debitnom karticom

            Metoda plaćanja je sigurna i zaštićena.
        """.trimIndent(),
        "Koliko traje dostava?" to """
            Dostava traje 2-5 radnih dana, ovisno o lokaciji.
        """.trimIndent(),
        "Kako kontaktirati podršku?" to """
            Našu korisničku podršku možeš kontaktirati:
            E-mail: support@nuvio.com
            Telefon: +385 1 123-4567
            Radno vrijeme: pon-pet, 9:00-17:00

            Naš tim će ti rado pomoći.
        """.trimIndent()
    )
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CustomTopBar(
                title = stringResource(R.string.support_title),
                showBack = true,
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = null,
                        tint = AccentColor,
                        modifier = Modifier.size(27.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Najčešće postavljena pitanja",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                faqQuestions.forEach { (question, answer) ->
                    var expanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardItemBackgroundLight)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = !expanded }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = question,
                                    modifier = Modifier.weight(1f),
                                    color = Black
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_down),
                                    contentDescription = null,
                                    tint = Black,
                                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                                )
                            }

                            if (expanded) {
                                Text(
                                    text = answer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = AccentColor,
                        modifier = Modifier.size(27.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kontakt podrška",
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardItemBackgroundLight, RoundedCornerShape(6.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = AccentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(text = "Email")
                            Text(
                                text = "support@nuvio.com",
                                color = AccentColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = null,
                            tint = AccentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(text = "Telefon")
                            Text(
                                text = "+385 1 123-4567",
                                color = AccentColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(text = "Radno vrijeme")
                            Text(
                                text = "Ponedjeljak - petak",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "8:00 - 17:00",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}