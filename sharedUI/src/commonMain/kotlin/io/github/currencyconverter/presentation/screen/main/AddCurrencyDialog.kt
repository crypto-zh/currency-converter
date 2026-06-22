package io.github.currencyconverter.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import currency_converter.sharedui.generated.resources.Res
import currency_converter.sharedui.generated.resources.add_currency
import currency_converter.sharedui.generated.resources.nothing_was_found
import currency_converter.sharedui.generated.resources.search
import io.github.currencyconverter.presentation.AppColors.CardDark
import io.github.currencyconverter.presentation.AppColors.DividerColor
import io.github.currencyconverter.presentation.AppColors.Gold
import io.github.currencyconverter.presentation.AppColors.GoldMuted
import io.github.currencyconverter.presentation.AppColors.SurfaceDark
import io.github.currencyconverter.presentation.AppColors.TextPrimary
import io.github.currencyconverter.presentation.AppColors.TextSecondary
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddCurrencyDialog(
    availableCurrencies: List<String>,
    onAddCurrency: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filtered = remember(query) {
        if (query.isBlank()) {
            availableCurrencies
        } else {
            availableCurrencies.filter { it.contains(query.trim(), ignoreCase = true) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(SurfaceDark)
                    .padding(top = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.add_currency),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W500
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.search),
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    query = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Gold,
                        unfocusedBorderColor = DividerColor,
                        cursorColor = Gold,
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark,
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

                if (filtered.isEmpty()) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.nothing_was_found),
                            color = TextSecondary,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    LazyColumn {
                        items(
                            items = filtered,
                            key = { it }
                        ) { currency ->
                            CurrencyRow(
                                currency = currency,
                                onClick = {
                                    onAddCurrency(currency)
                                    onDismiss()
                                }
                            )
                            HorizontalDivider(
                                color = DividerColor,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyRow(
    currency: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GoldMuted.copy(alpha = 0.25f))
        ) {
            Text(
                text = currency.take(3),
                color = Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.W500
            )
        }

        Spacer(Modifier.width(14.dp))

        Text(
            text = currency,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
    }
}