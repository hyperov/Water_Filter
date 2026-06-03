package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithCandles
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@Composable
fun DashboardScreen(
    viewModel: com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel,
    onAddFilter: () -> Unit
) {
    val filters by viewModel.allFiltersWithCandles.collectAsStateWithLifecycle()
    val selectedFilterId by viewModel.selectedFilterId.collectAsStateWithLifecycle()

    var showDeleteConfirmation by remember { mutableStateOf<Filter?>(null) }

    DashboardScreenContent(
        filters = filters,
        selectedFilterId = selectedFilterId,
        onFilterSelected = { viewModel.selectFilter(it) },
        onAddFilter = onAddFilter,
        onDeleteFilter = { showDeleteConfirmation = it },
        onMarkAsChanged = { viewModel.updateCandle(it) }
    )

    showDeleteConfirmation?.let { filter ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text(text = stringResource(R.string.delete_filter_confirm_title)) },
            text = {
                Text(
                    text = stringResource(
                        R.string.delete_filter_confirm_message,
                        filter.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFilter(filter)
                        showDeleteConfirmation = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = null }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    filters: List<FilterWithCandles>,
    selectedFilterId: Long?,
    onFilterSelected: (Long) -> Unit,
    onAddFilter: () -> Unit,
    onDeleteFilter: (Filter) -> Unit,
    onMarkAsChanged: (Candle) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    val selectedFilterIndex = remember(filters, selectedFilterId) {
        if (selectedFilterId != null) {
            val index = filters.indexOfFirst { it.filter.id == selectedFilterId }
            if (index != -1) index else 0
        } else {
            0
        }
    }

    val currentFilter = filters.getOrNull(selectedFilterIndex)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    if (currentFilter != null) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = stringResource(R.string.settings)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = stringResource(R.string.delete_filter),
                                            color = MaterialTheme.colorScheme.error
                                        ) 
                                    },
                                    onClick = {
                                        showMenu = false
                                        onDeleteFilter(currentFilter.filter)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddFilter,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text(text = stringResource(R.string.add_filter)) },
                shape = RoundedCornerShape(24.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filters.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.no_filters))
                }
            } else {
                // Filter Selector
                ScrollableTabRow(
                    selectedTabIndex = selectedFilterIndex,
                    edgePadding = 24.dp,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {}
                ) {
                    filters.forEachIndexed { index, filterWithCandles ->
                        Tab(
                            selected = selectedFilterIndex == index,
                            onClick = { onFilterSelected(filterWithCandles.filter.id) },
                            text = {
                                Text(
                                    text = filterWithCandles.filter.name,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        )
                    }
                }

                currentFilter?.let {
                    DashboardContent(
                        filter = it,
                        onMarkAsChanged = onMarkAsChanged
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    WaterFilterTheme {
        DashboardScreenContent(
            filters = listOf(
                FilterWithCandles(
                    filter = Filter(1L, "Home", FilterType.CANDLES_3),
                    candles = emptyList()
                )
            ),
            selectedFilterId = 1L,
            onFilterSelected = {},
            onAddFilter = {},
            onDeleteFilter = {},
            onMarkAsChanged = {}
        )
    }
}
