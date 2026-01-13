package com.example.nuviofrontend.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.core.R
import com.example.core.ui.components.CustomTextField
import com.example.core.ui.components.SmallCustomButton
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.ButtonColorDark
import com.example.core.ui.theme.CardItemBackgroundLight
import com.example.core.ui.theme.White

@Composable
fun AddCardDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (cardName: String, cardNumber: String, month: Int, year: Int, fullName: String) -> Unit,
    backendError: CardErrors? = null
) {
    if (!showDialog) return

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .width(362.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.surfaceDim)
                .padding(16.dp)
        ) {
            var cardName by remember { mutableStateOf("") }
            var cardNumber by remember { mutableStateOf("") }
            var month by remember { mutableStateOf("") }
            var year by remember { mutableStateOf("") }
            var fullName by remember { mutableStateOf("") }

            var cardNameError by remember { mutableStateOf<String?>(null) }
            var cardNumberError by remember { mutableStateOf<String?>(null) }
            var expiryError by remember { mutableStateOf<String?>(null) }
            var fullNameError by remember { mutableStateOf<String?>(null) }

            val cardNameEmptyMsg = stringResource(R.string.card_name_empty)
            val cardNumberEmptyMsg = stringResource(R.string.card_number_empty)
            val cardNumberLengthMsg = stringResource(R.string.card_number_length)
            val expiryEmptyMsg = stringResource(R.string.expiry_empty)
            val fullNameEmptyMsg = stringResource(R.string.full_name_empty)

            LaunchedEffect(backendError) {
                cardNumberError = backendError?.cardNumberError
                expiryError = backendError?.expiryError
            }

            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val cardLogo = when {
                        cardNumber.startsWith("4") -> R.drawable.visa_logo
                        cardNumber.startsWith("5") -> R.drawable.mastercard_logo
                        else -> R.drawable.add_new_card
                    }
                    Image(
                        painter = painterResource(id = cardLogo),
                        contentDescription = "Add Card Icon",
                        modifier = Modifier.size(width = 20.dp, height = 16.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.new_card),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Divider(color = BackgroundNavDark)
                Spacer(modifier = Modifier.height(7.dp))


                CustomTextField(
                    value = cardName,
                    onValueChange = {
                        cardName = it
                        cardNameError = null
                    },
                    placeholder = stringResource(R.string.card_name),
                    label = stringResource(R.string.card_name),
                    isError = cardNameError != null,
                    errorMessage = cardNameError
                )

                CustomTextField(
                    value = cardNumber,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }
                        if (digitsOnly.length <= 16) {
                            cardNumber = digitsOnly
                        }
                        cardNumberError = null
                    },
                    placeholder = "XXXX-XXXX-XXXX-XXXX",
                    label = stringResource(R.string.card_number),
                    isError = cardNumberError != null,
                    errorMessage = cardNumberError,
                    visualTransformation =  CardNumberVisualTransformation()
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    CustomTextField(
                        value = month,
                        onValueChange = {
                            month = it.filter { ch -> ch.isDigit() }
                            expiryError = null
                        },
                        placeholder = stringResource(R.string.month),
                        label = stringResource(R.string.due_date),
                        modifier = Modifier.weight(1f),
                        isError = expiryError != null,
                        errorMessage = expiryError
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomTextField(
                        value = year,
                        onValueChange = {
                            year = it.filter { ch -> ch.isDigit() }
                            expiryError = null
                        },
                        placeholder = stringResource(R.string.year),
                        label = "",
                        modifier = Modifier.weight(1f),
                        isError = expiryError != null,
                        errorMessage = expiryError
                    )
                }

                CustomTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = null
                    },
                    placeholder = stringResource(R.string.full_name),
                    label = stringResource(R.string.full_name),
                    isError = fullNameError != null,
                    errorMessage = fullNameError
                )

                Spacer(modifier = Modifier.height(7.dp))
                Divider(color = BackgroundNavDark)
                Spacer(modifier = Modifier.height(7.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallCustomButton(
                        text = stringResource(R.string.cancel),
                        onClick = onDismiss,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    )

                    SmallCustomButton(
                        text = stringResource(R.string.save_button),
                        onClick = {
                            val expMonth = month.toIntOrNull()
                            val expYear = year.toIntOrNull()
                            var hasError = false

                            if (cardName.isBlank()) {
                                cardNameError = cardNameEmptyMsg
                                hasError = true
                            }
                            if (cardNumber.isBlank()) {
                                cardNumberError = cardNumberEmptyMsg
                                hasError = true
                            } else if (cardNumber.length != 16) {
                                cardNumberError = cardNumberLengthMsg
                                hasError = true
                            }

                            if (month.isBlank() || year.isBlank()) {
                                expiryError = expiryEmptyMsg
                                hasError = true
                            }

                            if (fullName.isBlank()) {
                                fullNameError = fullNameEmptyMsg
                                hasError = true
                            }

                            if (!hasError && expMonth != null && expYear != null) {
                                onSave(cardName, cardNumber, expMonth, expYear, fullName)
                            }
                        }
                    )
                }
            }
        }
    }
}

class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val transformed = digits.chunked(4).joinToString(" ")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val spaces = (offset - 1) / 4
                return (offset + spaces).coerceAtMost(transformed.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var original = offset
                original -= transformed.take(offset).count { it == ' ' }
                return original.coerceAtMost(digits.length)
            }
        }

        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}