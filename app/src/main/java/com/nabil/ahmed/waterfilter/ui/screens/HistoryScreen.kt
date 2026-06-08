package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.FilterWithCandles
import com.nabil.ahmed.waterfilter.data.entity.MaintenanceLog
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: WaterFilterViewModel,
    onNavigateToDashboard: () -> Unit = {}
) {
    val logs by viewModel.allMaintenanceLogs.collectAsStateWithLifecycle()
    val filters by viewModel.allFiltersWithCandles.collectAsStateWithLifecycle()

    HistoryScreenContent(
        logs = logs,
        filters = filters,
        onDeleteLog = { viewModel.deleteMaintenanceLog(it) },
        onNavigateToDashboard = onNavigateToDashboard
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreenContent(
    logs: List<MaintenanceLog>,
    filters: List<FilterWithCandles>,
    onDeleteLog: (MaintenanceLog) -> Unit,
    onNavigateToDashboard: () -> Unit = {}
) {
    var selectedFilterId by remember { mutableLongStateOf(-1L) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, selectedFilterId, searchQuery) {
        logs.filter { log ->
            val matchesFilter = selectedFilterId == -1L || log.filterId == selectedFilterId
            val matchesSearch = log.candleName.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val groupedLogs = remember(filteredLogs) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("ar"))
        filteredLogs.groupBy { log ->
            sdf.format(Date(log.changeDateTimestamp))
        }
    }

    val totalChanges = logs.size
    val mostMaintainedFilterName = remember(logs, filters) {
        if (logs.isEmpty()) ""
        else {
            logs.groupBy { it.filterId }
                .maxByOrNull { it.value.size }
                ?.key?.let { id -> filters.find { it.filter.id == id }?.filter?.name }
                ?: ""
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Selector Chips
            ScrollableTabRow(
                selectedTabIndex = if (selectedFilterId == -1L) 0 else filters.indexOfFirst { it.filter.id == selectedFilterId } + 1,
                edgePadding = 24.dp,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {}
            ) {
                FilterChip(
                    selected = selectedFilterId == -1L,
                    onClick = { selectedFilterId = -1L },
                    label = { Text(stringResource(R.string.all)) },
                    modifier = Modifier.padding(horizontal = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )

                filters.forEach { filterWithCandles ->
                    FilterChip(
                        selected = selectedFilterId == filterWithCandles.filter.id,
                        onClick = { selectedFilterId = filterWithCandles.filter.id },
                        label = { Text(filterWithCandles.filter.name) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Insights Card
            if (logs.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.total_maintenance_events),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = totalChanges.toString(),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        if (mostMaintainedFilterName.isNotEmpty()) {
                            VerticalDivider(
                                modifier = Modifier.height(40.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = stringResource(R.string.most_active_filter),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = mostMaintainedFilterName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            if (filteredLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(if (searchQuery.isEmpty()) R.string.no_history else R.string.no_results),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.no_history_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = onNavigateToDashboard,
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Icon(Icons.Rounded.Home, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.go_to_dashboard))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    groupedLogs.forEach { (month, monthLogs) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = month,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }
                        }
                        
                        items(monthLogs, key = { it.id }) { log ->
                            val currentOnDeleteLog by rememberUpdatedState(onDeleteLog)
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        currentOnDeleteLog(log)
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .height(IntrinsicSize.Min)
                            ) {
                                // Static Timeline line
                                Box(
                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .width(2.dp)
                                        .fillMaxHeight()
                                        .background(
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(1.dp)
                                        )
                                )

                                Spacer(modifier = Modifier.width(24.dp))

                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    backgroundContent = {
                                        val color = when (dismissState.dismissDirection) {
                                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                            else -> Color.Transparent
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(vertical = 8.dp)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(color),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                modifier = Modifier.padding(end = 16.dp),
                                                tint = MaterialTheme.colorScheme.surface
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 8.dp)
                                ) {
                                    val filterIndex = filters.indexOfFirst { it.filter.id == log.filterId }
                                    val accentColor = when (filterIndex % 3) {
                                        0 -> MaterialTheme.colorScheme.primary
                                        1 -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }

                                    HistoryLogCard(
                                        log = log,
                                        filterName = filters.find { it.filter.id == log.filterId }?.filter?.name ?: "",
                                        accentColor = accentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun HistoryScreenEmptyPreview() {
    WaterFilterTheme {
        HistoryScreenContent(
            logs = emptyList(),
            filters = emptyList(),
            onDeleteLog = {}
        )
    }
}
