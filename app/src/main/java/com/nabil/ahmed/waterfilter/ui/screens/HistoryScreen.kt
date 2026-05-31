package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: WaterFilterViewModel
) {
    val logs by viewModel.allMaintenanceLogs.collectAsStateWithLifecycle()
    val filters by viewModel.allFiltersWithStages.collectAsStateWithLifecycle()
    
    var selectedFilterId by remember { mutableLongStateOf(-1L) } // -1 for All
    
    val filteredLogs = if (selectedFilterId == -1L) {
        logs
    } else {
        logs.filter { it.filterId == selectedFilterId }
    }

    val groupedLogs = remember(filteredLogs) {
        filteredLogs.groupBy { log ->
            val date = Date(log.changeDateTimestamp)
            val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            sdf.format(date)
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
                },
                actions = {
                    IconButton(onClick = { /* Filter Logic if needed */ }) {
                        Icon(imageVector = Icons.Rounded.FilterList, contentDescription = stringResource(R.string.filter_all))
                    }
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
                // "All" Chip
                FilterChip(
                    selected = selectedFilterId == -1L,
                    onClick = { selectedFilterId = -1L },
                    label = { Text(stringResource(R.string.all)) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                filters.forEach { filterWithStages ->
                    FilterChip(
                        selected = selectedFilterId == filterWithStages.filter.id,
                        onClick = { selectedFilterId = filterWithStages.filter.id },
                        label = { Text(filterWithStages.filter.name) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            if (filteredLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.history_img),
                            contentDescription = null,
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.no_history))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedLogs.forEach { (month, monthLogs) ->
                        item {
                            Text(
                                text = month,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(monthLogs) { log ->
                            HistoryLogCard(
                                log = log,
                                filterName = filters.find { it.filter.id == log.filterId }?.filter?.name ?: "",
                                onDelete = { viewModel.deleteMaintenanceLog(log) }
                            )
                        }
                    }
                }
            }
        }
    }
}
