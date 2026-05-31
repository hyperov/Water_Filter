package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WaterFilterViewModel,
    onAddFilter: () -> Unit
) {
    val filters by viewModel.allFiltersWithStages.collectAsStateWithLifecycle()
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFilter,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.add_filter))
            }
        }
    ) { innerPadding ->
        if (filters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_filters))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Filter Selector (Scrollable Tabs)
                ScrollableTabRow(
                    selectedTabIndex = selectedFilterIndex,
                    edgePadding = 24.dp,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {}
                ) {
                    filters.forEachIndexed { index, filterWithStages ->
                        Tab(
                            selected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                    .clip(RoundedCornerShape(24.dp)),
                                color = if (selectedFilterIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = filterWithStages.filter.name,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                currentFilter?.let { filter ->
                    DashboardContent(
                        filter = filter,
                        onMarkAsChanged = { viewModel.updateStage(it) }
                    )
                }
            }
        }
    }
}
