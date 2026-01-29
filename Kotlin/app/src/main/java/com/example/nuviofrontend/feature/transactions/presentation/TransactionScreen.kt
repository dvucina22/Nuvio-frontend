package com.example.nuviofrontend.feature.transactions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.presentation.AuthViewModel
import com.example.core.R
import com.example.core.transactions.dto.TransactionListItem
import com.example.core.ui.components.CustomPopupWarning
import com.example.core.ui.components.CustomRangeSlider
import com.example.core.ui.components.CustomTopBar
import com.example.core.ui.components.IconActionBox
import com.example.core.ui.components.SearchField
import com.example.core.ui.components.TransactionCard
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.IconSelectedTintDark
import com.example.core.ui.theme.SelectedItemBackgroundDark
import com.example.core.ui.theme.White
import com.example.core.ui.theme.WhiteSoft
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    onTransactionClick: (Long) -> Unit,
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFilterSheet by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(TransactionsFilterState()) }
    val scope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val isAdmin = authState.isAdmin

    var transactionToVoid by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(showFilterSheet) {
        if (showFilterSheet) {
            viewModel.ensureFilterDataLoaded()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CustomTopBar(
                title = stringResource(R.string.transactions_title),
                showBack = true,
                onBack = onBack
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchField(
                        modifier = Modifier.weight(1f),
                        value = state.query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = stringResource(R.string.search_transactions),
                        label = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconActionBox(
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = stringResource(R.string.filter),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = IconSelectedTintDark)
                        }
                    }

                    state.results.isEmpty() &&
                            state.query.isNotBlank() &&
                            state.error == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_results),
                                color = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        }
                    }

                    state.results.isNotEmpty() -> {
                        TransactionsResultsList(
                            items = state.results,
                            isAdmin = isAdmin,
                            isLoadingMore = state.isLoadingMore,
                            onVoidClick = { transactionToVoid = it },
                            onLoadMore = { viewModel.loadMore() },
                            onClick = { onTransactionClick(it) }
                        )
                    }
                }
            }
        }

        if (state.error != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                action = {
                    TextButton(
                        onClick = { viewModel.clearError() },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = WhiteSoft
                        )
                    ) {
                        Text(text = stringResource(R.string.dismiss))
                    }
                }
            ) {
                Text(text = state.error ?: "")
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                TransactionsFilterSheetContent(
                    sheetState = sheetState,
                    filterState = filterState,
                    statusOptions = state.statusOptions,
                    typeOptions = state.typeOptions,
                    isLoadingInitialData = state.isFilterDataLoading,
                    onFilterStateChange = { filterState = it },
                    onReset = {
                        val reset = TransactionsFilterState()
                        filterState = reset
                        viewModel.applyFilters(reset)
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showFilterSheet = false
                        }
                    },
                    onApply = {
                        viewModel.applyFilters(filterState)
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showFilterSheet = false
                        }
                    }
                )
            }
        }
        if (transactionToVoid != null) {
            CustomPopupWarning(
                message = stringResource(R.string.confirm_void_transaction),
                confirmText = stringResource(R.string.next),
                dismissText = stringResource(R.string.cancel),
                onDismiss = { transactionToVoid = null },
                onConfirm = {
                    viewModel.voidTransaction(transactionToVoid!!)
                    transactionToVoid = null
                }
            )
        }

    }
}

@Composable
private fun TransactionsResultsList(
    items: List<TransactionListItem>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onClick: (Long) -> Unit,
    isAdmin: Boolean,
    onVoidClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
    ) {
        itemsIndexed(items) { index, item ->
            if (index == items.lastIndex) {
                LaunchedEffect(key1 = index) {
                    onLoadMore()
                }
            }

            TransactionCard(
                transaction = item,
                onClick = { onClick(item.id) },
                showMenu = false,
                isAdmin = isAdmin,
                onVoidClick = { onVoidClick(item.id) }
            )
        }



        if (isLoadingMore) {
            items(6) {
                TransactionPlaceholderRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    onClick = null
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

@Composable
private fun TransactionPlaceholderRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    val clickableMod =
        if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    Column(
        modifier = clickableMod
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .height(14.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.10f))
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
        )
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f))
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun TransactionsFilterSheetContent(
    sheetState: SheetState,
    filterState: TransactionsFilterState,
    statusOptions: List<String>,
    typeOptions: List<String>,
    isLoadingInitialData: Boolean,
    onFilterStateChange: (TransactionsFilterState) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(SelectedItemBackgroundDark)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.filter_transactions),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoadingInitialData && statusOptions.isEmpty() && typeOptions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IconSelectedTintDark)
            }
        } else {
            FilterSection(title = stringResource(R.string.transaction_status)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOptions.forEach { value ->
                        val selected = filterState.selectedStatuses.contains(value)
                        FilterChip(
                            label = statusLabel(value),
                            selected = selected,
                            onClick = {
                                val next =
                                    if (selected) filterState.selectedStatuses - value
                                    else filterState.selectedStatuses + value
                                onFilterStateChange(filterState.copy(selectedStatuses = next))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = stringResource(R.string.transaction_type)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    typeOptions.forEach { value ->
                        val selected = filterState.selectedTypes.contains(value)
                        FilterChip(
                            label = typeLabel(value),
                            selected = selected,
                            onClick = {
                                val next =
                                    if (selected) filterState.selectedTypes - value
                                    else filterState.selectedTypes + value
                                onFilterStateChange(filterState.copy(selectedTypes = next))
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = stringResource(R.string.amount_range)) {
                CustomRangeSlider(
                    value = filterState.amountRange,
                    onValueChange = { onFilterStateChange(filterState.copy(amountRange = it)) },
                    valueRange = 0f..500000f
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${filterState.amountRange.start.toLong()} - ${filterState.amountRange.endInclusive.toLong()}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(title = stringResource(R.string.product_count_range)) {
                CustomRangeSlider(
                    value = filterState.productCountRange,
                    onValueChange = { onFilterStateChange(filterState.copy(productCountRange = it)) },
                    valueRange = 0f..50f
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${filterState.productCountRange.start.toInt()} - ${filterState.productCountRange.endInclusive.toInt()}",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.apply),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) AccentColor
                else MaterialTheme.colorScheme.surfaceContainerLowest
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) White else MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun statusLabel(raw: String): String {
    val v = raw.trim().uppercase()
    return when (v) {
        "PENDING" -> stringResource(R.string.status_pending)
        "APPROVED" -> stringResource(R.string.status_approved)
        "DECLINED" -> stringResource(R.string.status_declined)
        "VOIDED" -> stringResource(R.string.status_voided)
        else -> raw
    }
}

@Composable
private fun typeLabel(raw: String): String {
    val v = raw.trim().uppercase()
    return when (v) {
        "SALE" -> stringResource(R.string.type_sale)
        "VOID" -> stringResource(R.string.type_void)
        else -> raw
    }
}
