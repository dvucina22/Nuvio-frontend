@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.R
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.IconActionBox
import com.example.core.ui.components.SearchField
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.WhiteSoft
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.collections.map

data class SavedCardUi(
    val id: String,
    val title: String,
    val brandName : String,
    val maskedNumber: String,
    val expiry: String? = null,
    val isPrimary: Boolean = false
)

@Composable
fun CardScreen(
    viewModel: CardViewModel,
    onBack: () -> Unit
) {
    var showAddCardDialog by remember { mutableStateOf(false) }
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }

    val cards by viewModel.cards.collectAsState()
    var query by remember { mutableStateOf("") }

    val filteredCards = cards.filter { card ->
        card.cardName.contains(query, ignoreCase = true)
    }.map { card ->
        SavedCardUi(
            id = card.id.toString(),
            title = card.cardName,
            maskedNumber = "XXXX-XXXX-XXXX-${card.lastFourDigits}",
            expiry = "${card.expirationMonth.toString().padStart(2, '0')}/${card.expirationYear.toString().takeLast(2)}",
            isPrimary = card.isPrimary,
            brandName = card.cardBrand
        )
    }

    val uiCards = cards.map { card ->
        SavedCardUi(
            id = card.id.toString(),
            title = card.cardName,
            maskedNumber = "XXXX-XXXX-XXXX-${card.lastFourDigits}",
            expiry = "${card.expirationMonth.toString().padStart(2, '0')}/${card.expirationYear.toString().takeLast(2)}",
            isPrimary = card.isPrimary,
            brandName = card.cardBrand
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            CustomTopBar(
                title = stringResource(R.string.saved_cards_title),
                showBack = true,
                onBack = onBack
            )


            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Pretraži kartice",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconActionBox(
                    onClick = { showAddCardDialog = true },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCard,
                        contentDescription = "Dodaj karticu",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_cards),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredCards, key = { it.id }) { card ->
                        CardItem(
                            card = card,
                            onDelete = { confirmDeleteId = card.id },
                            onSetPrimary = { viewModel.setPrimaryCard(it) }
                        )
                    }
                    item{
                        Spacer(modifier = Modifier.height(110.dp))
                    }
                }
            }
        }

        if (showAddCardDialog) {
            AddCardDialog(
                showDialog = showAddCardDialog,
                onDismiss = {
                    showAddCardDialog = false
                    viewModel.clearError()
                },
                onSave = { cardName, cardNumber, month, year, fullName ->
                    viewModel.addCard(
                        cardName = cardName,
                        cardNumber = cardNumber,
                        expirationMonth = month,
                        expirationYear = year,
                        fullName = fullName,
                        isPrimary = true,
                        onSuccess = { showAddCardDialog = false }
                    )
                },
                backendError = viewModel.error.collectAsState().value
            )
        }

        if (confirmDeleteId != null) {
            CustomPopupWarning(
                title = stringResource(R.string.warning),
                message = stringResource(R.string.delete_card_confirm),
                confirmText = stringResource(R.string.next),
                dismissText = stringResource(R.string.cancel),
                onDismiss = { confirmDeleteId = null },
                onConfirm = {
                    viewModel.deleteCard(confirmDeleteId!!)
                    confirmDeleteId = null
                }
            )
        }
    }
}

@Composable
fun CardItem(
    card: SavedCardUi,
    onDelete: (String) -> Unit,
    onSetPrimary: (String) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val primaryAlpha by animateFloatAsState(targetValue = if (card.isPrimary) 1f else 0f)
    val primaryScale by animateFloatAsState(targetValue = if (card.isPrimary) 1.2f else 1f)

    @Composable
    fun MenuItem(
        icon: ImageVector,
        label: String,
        onClick: () -> Unit
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clickable {
                    menuOpen = false
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = WhiteSoft,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = WhiteSoft,
                fontSize = 12.sp
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .then(
                if (isCardExpired(card.expiry))
                    Modifier.border(1.dp, Error, RoundedCornerShape(8.dp))
                else Modifier.border(1.dp, MaterialTheme.colorScheme.surfaceDim, RoundedCornerShape(8.dp))
            ),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = getCardLogo(card.brandName),
                    contentDescription = "Card Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = card.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                )
                if (card.isPrimary) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.primary_card),
                        contentDescription = "Primarna kartica",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(13.dp).alpha(primaryAlpha).scale(primaryScale)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Box {
                    IconButton(
                        onClick = { menuOpen = true },
                        modifier = Modifier.size(15.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (menuOpen) {
                        Popup(
                            alignment = Alignment.TopEnd,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(Color(0xFF232323), RoundedCornerShape(6.dp))
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .width(IntrinsicSize.Max)
                            ) {
                                MenuItem(Icons.Default.Delete, stringResource(R.string.delete)) { onDelete(card.id) }
                                MenuItem(Icons.Default.Star, stringResource(R.string.set_as_primary)) { onSetPrimary(card.id) }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = BackgroundNavDark,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.card_number_label),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = card.maskedNumber,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.due_date),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = card.expiry ?: "--/--",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun getCardLogo(cardBrand: String): Painter {
    return when (cardBrand.lowercase()) {
        "visa" -> painterResource(R.drawable.visa_logo)
        "mastercard" -> painterResource(R.drawable.mastercard_logo)
        else -> painterResource(R.drawable.add_new_card)
    }
}

fun isCardExpired(expiry: String?): Boolean {
    if (expiry.isNullOrBlank()) return false

    val parts = expiry.split("/")
    if (parts.size != 2) return false

    val month = parts[0].toIntOrNull() ?: return false
    val year = parts[1].toIntOrNull() ?: return false

    val fullYear = if (year < 100) 2000 + year else year

    val today = Calendar.getInstance()
    val currentYear = today.get(Calendar.YEAR)
    val currentMonth = today.get(Calendar.MONTH) + 1

    return fullYear < currentYear || (fullYear == currentYear && month < currentMonth)
}
