package io.github.currencyconverter.presentation.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import currency_converter.sharedui.generated.resources.Res
import currency_converter.sharedui.generated.resources.app_name
import currency_converter.sharedui.generated.resources.downloading_currency
import currency_converter.sharedui.generated.resources.enter_amount
import currency_converter.sharedui.generated.resources.error_message
import currency_converter.sharedui.generated.resources.no_added_currencies
import currency_converter.sharedui.generated.resources.press_plus
import io.github.currencyconverter.presentation.AppColors.BackgroundDark
import io.github.currencyconverter.presentation.AppColors.CardDark
import io.github.currencyconverter.presentation.AppColors.ContentColor
import io.github.currencyconverter.presentation.AppColors.DangerRed
import io.github.currencyconverter.presentation.AppColors.DividerColor
import io.github.currencyconverter.presentation.AppColors.Gold
import io.github.currencyconverter.presentation.AppColors.GoldMuted
import io.github.currencyconverter.presentation.AppColors.SurfaceDark
import io.github.currencyconverter.presentation.AppColors.TextPrimary
import io.github.currencyconverter.presentation.AppColors.TextSecondary
import io.github.currencyconverter.presentation.model.CurrencyUIModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var availableCurrencies by remember { mutableStateOf<List<String>>(emptyList()) }
    val errorMessage = stringResource(Res.string.error_message)

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MainScreenEvent.NavigateToAdd -> {
                    showAddDialog = true
                    availableCurrencies = event.availableCurrencies
                }

                MainScreenEvent.ShowError -> {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }
    }
    if (showAddDialog) {
        AddCurrencyDialog(
            availableCurrencies = availableCurrencies,
            onAddCurrency = {
                viewModel.onIntent(MainScreenIntent.OnCurrencyAdded(it))
            },
            onDismiss = { showAddDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onIntent(MainScreenIntent.OnCurrencyAddClicked)
                },
                containerColor = Gold,
                contentColor = ContentColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = state.showLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Gold, strokeWidth = 2.dp)
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(Res.string.downloading_currency),
                        color = TextSecondary,
                        fontSize = 14.sp,
                    )
                }
            }

            AnimatedVisibility(
                visible = !state.showLoading && state.currencies.isEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                EmptyState()
            }

            AnimatedVisibility(
                visible = !state.showLoading && state.currencies.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CurrencyList(
                    currencies = state.currencies,
                    onValueChanged = { model ->
                        viewModel.onIntent(MainScreenIntent.OnCurrencyValueChanged(model))
                    },
                    onDelete = { name ->
                        viewModel.onIntent(MainScreenIntent.OnCurrencyDeleted(name))
                    }
                )
            }
        }
    }
}

@Composable
private fun CurrencyList(
    currencies: List<CurrencyUIModel>,
    onValueChanged: (CurrencyUIModel) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.app_name),
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = stringResource(Res.string.enter_amount),
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = currencies,
            key = { it.name }
        ) { model ->
            CurrencyCard(
                model = model,
                onValueChanged = onValueChanged,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun CurrencyCard(
    model: CurrencyUIModel,
    onValueChanged: (CurrencyUIModel) -> Unit,
    onDelete: (String) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldMuted.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = model.name.take(3),
                            color = Gold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W500
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = model.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { onDelete(model.name) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = DangerRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = model.value,
                onValueChange = { raw ->
                    val filtered = raw
                        .replace(',', '.')
                        .filter { it.isDigit() || it == '.' }
                        .let { s ->
                            val dotIdx = s.indexOf('.')
                            if (dotIdx >= 0) s.substring(0, dotIdx + 1) +
                                    s.substring(dotIdx + 1).filter { it.isDigit() }
                            else s
                        }
                    onValueChanged(model.copy(value = filtered))
                },
                placeholder = {
                    Text("0.00", color = TextSecondary, fontSize = 20.sp)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = DividerColor,
                    cursorColor = Gold,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text("€ $ ¥", fontSize = 32.sp, color = Gold.copy(alpha = 0.5f))
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(Res.string.no_added_currencies),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.W500
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(Res.string.press_plus),
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DateChip(
    date: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GoldMuted.copy(alpha = 0.25f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = date,
                color = Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        }
    }
}