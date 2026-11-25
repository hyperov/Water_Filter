package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun HistoryScreenContent(
    logs: List<MaintenanceLog>,
    filters: List<FilterWithCandles>,
    onDeleteLog: (MaintenanceLog) -> Unit,
    onNavigateToDashboard: () -> Unit = {}
) {
    var selectedFilterId by rememberSaveable { mutableLongStateOf(-1L) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filterNameMap = remember(filters) {
        filters.associate { it.filter.id to it.filter.name }
    }

    val uniqueLogs = remember(logs) {
        logs.distinctBy { it.id }
            .distinctBy { Triple(it.filterId, it.candleNumber, it.changeDateTimestamp) }
    }

    val filteredLogs = remember(uniqueLogs, selectedFilterId, searchQuery) {
        uniqueLogs.filter { log ->
            val matchesFilter = selectedFilterId == -1L || log.filterId == selectedFilterId
            val matchesSearch = searchQuery.isEmpty() ||
                    log.candleName.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val groupedLogs = remember(filteredLogs) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("ar"))
        filteredLogs.groupBy { log ->
            sdf.format(Date(log.changeDateTimestamp))
        }
    }

    val totalChanges = uniqueLogs.size

    val mostMaintainedCandle = remember(uniqueLogs) {
        if (uniqueLogs.isEmpty()) null
        else {
            uniqueLogs.groupBy { it.candleName }
                .maxByOrNull { it.value.size }
        }
    }

    val mostReplacedCandleName = mostMaintainedCandle?.key ?: "-"
    val mostReplacedCount = mostMaintainedCandle?.value?.size ?: 0
    val mostReplacedStage = mostMaintainedCandle?.value?.firstOrNull()?.candleNumber ?: 0

    val dashPathEffect = remember { PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.candles_maintenance_log_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = stringResource(R.string.candles_maintenance_log_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Category Filter Pills (Using LazyRow instead of ScrollableTabRow)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedFilterId == -1L,
                        onClick = { selectedFilterId = -1L },
                        label = {
                            Text(
                                text = "${stringResource(R.string.all)} (${uniqueLogs.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = Color(0xFF00A86B),
                            selectedLabelColor = Color.White,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            enabled = true,
                            selected = selectedFilterId == -1L
                        )
                    )
                }

                items(filters, key = { it.filter.id }) { filterWithCandles ->
                    FilterChip(
                        selected = selectedFilterId == filterWithCandles.filter.id,
                        onClick = { selectedFilterId = filterWithCandles.filter.id },
                        label = {
                            Text(
                                text = filterWithCandles.filter.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedContainerColor = Color(0xFF00A86B),
                            selectedLabelColor = Color.White,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            enabled = true,
                            selected = selectedFilterId == filterWithCandles.filter.id
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top Insights Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FDF4)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Column 1: Total Maintenance
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.total_maintenance),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF475569),
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = totalChanges.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A86B),
                                fontSize = 26.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = stringResource(R.string.documented_operation),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            )
                        }
                    }

                    VerticalDivider(
                        modifier = Modifier.height(55.dp),
                        thickness = 1.dp,
                        color = Color(0xFFDCFCE7)
                    )

                    // Column 2: Most Replaced
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(
                            text = stringResource(R.string.most_replaced),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF475569),
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mostReplacedCandleName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            ),
                            maxLines = 1
                        )
                        if (mostReplacedStage > 0) {
                            Text(
                                text = stringResource(R.string.stage_number, mostReplacedStage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Text(
                            text = if (mostReplacedCount > 0) stringResource(
                                R.string.times_count,
                                mostReplacedCount
                            ) else "-",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier.height(55.dp),
                        thickness = 1.dp,
                        color = Color(0xFFDCFCE7)
                    )

                    // Column 3: Compliance Rate
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.on_time_compliance),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF475569),
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (totalChanges > 0) "96%" else "-",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A86B),
                                fontSize = 26.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = stringResource(R.string.very_excellent),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            )
                        }
                    }
                }
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_log_placeholder),
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8)
                        )
                    },
                    leadingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF00A86B)
                    )
                )
            }

            // Main Content Timeline List
            Box(modifier = Modifier.weight(1f)) {
                if (filteredLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = Color(0xFFECFDF5)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(52.dp),
                                        tint = Color(0xFF00A86B)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = stringResource(if (searchQuery.isEmpty()) R.string.no_history else R.string.no_results),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = onNavigateToDashboard,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00A86B)
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 24.dp,
                                        vertical = 12.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.go_to_dashboard),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
                    ) {
                        groupedLogs.forEach { (month, monthLogs) ->
                            stickyHeader {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 20.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF00A86B))
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = month,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFE2E8F0)
                                        ) {
                                            Text(
                                                text = stringResource(
                                                    R.string.times_count,
                                                    monthLogs.size
                                                ),
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 4.dp
                                                ),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF475569)
                                                )
                                            )
                                        }


                                    }
                                }
                            }

                            itemsIndexed(monthLogs, key = { _, log -> log.id }) { _, log ->

                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp, vertical = 6.dp)
                                        .height(IntrinsicSize.Min)
                                ) {
                                    // Timeline Dotted Line
                                    Box(
                                        modifier = Modifier
                                            .width(14.dp)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Canvas(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(2.dp)
                                        ) {
                                            drawLine(
                                                color = Color(0xFFCBD5E1),
                                                start = Offset(0f, 0f),
                                                end = Offset(0f, size.height),
                                                pathEffect = dashPathEffect,
                                                strokeWidth = 2.dp.toPx()
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    HistoryLogCard(
                                        log = log,
                                        filterName = filterNameMap[log.filterId] ?: "",
                                        onDelete = { onDeleteLog(log) },
                                        modifier = Modifier.weight(1f)
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
