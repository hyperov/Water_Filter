package com.nabil.ahmed.waterfilter.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.entity.Candle
import com.nabil.ahmed.waterfilter.data.entity.Filter
import com.nabil.ahmed.waterfilter.data.entity.FilterWithCandles
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Alignment
import com.nabil.ahmed.waterfilter.data.util.CandleNumber
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: com.nabil.ahmed.waterfilter.ui.viewmodel.WaterFilterViewModel,
    onAddFilter: () -> Unit
) {
    val filters by viewModel.allFiltersWithCandles.collectAsStateWithLifecycle()
    val selectedFilterId by viewModel.selectedFilterId.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showDeleteConfirmation by remember { mutableStateOf<Filter?>(null) }
    var editingCandle by remember { mutableStateOf<Candle?>(null) }
    var renamingCandle by remember { mutableStateOf<Candle?>(null) }
    var editingLifespanCandle by remember { mutableStateOf<Candle?>(null) }
    var addingCandleFilter by remember { mutableStateOf<FilterWithCandles?>(null) }
    var deletingCandle by remember { mutableStateOf<Candle?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var parties by remember { mutableStateOf<List<Party>>(emptyList()) }

    Box(modifier = Modifier.fillMaxSize()) {
        DashboardScreenContent(
            filters = filters,
            selectedFilterId = selectedFilterId,
            isLoading = isLoading,
            snackbarHostState = snackbarHostState,
            onFilterSelected = { viewModel.selectFilter(it) },
            onAddFilter = onAddFilter,
            onDeleteFilter = { showDeleteConfirmation = it },
            onMarkAsChanged = { candle ->
                val oldTimestamp = candle.lastChangedTimestamp
                viewModel.updateCandle(candle)
                
                // Trigger Confetti
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xFFFCE18A.toInt(), 0xFFFF726D.toInt(), 0xFFF4306D.toInt(), 0xFFB48DEF.toInt()),
                        position = Position.Relative(0.5, 0.3),
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                    )
                )

                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "تم تغيير الشمعة اليوم بنجاح",
                        actionLabel = "تراجع",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.updateCandle(candle, oldTimestamp)
                    }
                }
            },
            onEditDate = { editingCandle = it },
            onRenameCandle = { renamingCandle = it },
            onEditLifespan = { editingLifespanCandle = it },
            onAddCandleToFilter = { filterWithCandles -> addingCandleFilter = filterWithCandles },
            onDeleteCandle = { candle -> deletingCandle = candle }
        )

        if (parties.isNotEmpty()) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = parties
            )
            LaunchedEffect(parties) {
                kotlinx.coroutines.delay(3000)
                parties = emptyList()
            }
        }
    }

    editingCandle?.let { candle ->
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = candle.lastChangedTimestamp
        )
        
        DatePickerDialog(
            onDismissRequest = { editingCandle = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { newTimestamp ->
                            val oldTimestamp = candle.lastChangedTimestamp
                            viewModel.updateCandle(candle, newTimestamp)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "تم تحديث التاريخ بنجاح",
                                    actionLabel = "تراجع",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.updateCandle(candle, oldTimestamp)
                                }
                            }
                        }
                        editingCandle = null
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCandle = null }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = { 
                    Text(
                        text = stringResource(R.string.edit_maintenance_date),
                        modifier = Modifier.padding(PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp))
                    )
                }
            )
        }
    }

    editingLifespanCandle?.let { candle ->
        var selectedLifespan by remember { mutableStateOf(candle.lifespanDays) }
        AlertDialog(
            onDismissRequest = { editingLifespanCandle = null },
            title = { Text(text = stringResource(R.string.edit_candle_lifespan)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.candle_lifespan),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Lifespan.entries.forEach { lifespan ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedLifespan == lifespan,
                                onClick = { selectedLifespan = lifespan }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(lifespan.resId))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateCandleLifespan(candle, selectedLifespan)
                        editingLifespanCandle = null
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingLifespanCandle = null }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    addingCandleFilter?.let { filterWithCandles ->
        val nextNumberInt = filterWithCandles.candles.size + 1
        val nextCandleNumber = CandleNumber.fromInt(nextNumberInt)
        val defaultName = stringResource(nextCandleNumber.nameResId)
        
        var candleName by remember { mutableStateOf(defaultName) }
        var selectedLifespan by remember { mutableStateOf(Lifespan.MONTHS_6) }
        var maintenanceTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

        AlertDialog(
            onDismissRequest = { addingCandleFilter = null },
            title = { Text(text = stringResource(R.string.add_new_candle)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = candleName,
                        onValueChange = { candleName = it },
                        label = { Text(stringResource(R.string.candle)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.candle_lifespan),
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Column {
                        Lifespan.entries.forEach { lifespan ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedLifespan == lifespan,
                                    onClick = { selectedLifespan = lifespan }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(lifespan.resId))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (candleName.isNotBlank() && filterWithCandles.candles.size < 7) {
                            viewModel.addCandleToFilter(
                                filterId = filterWithCandles.filter.id,
                                candleNumber = nextCandleNumber,
                                name = candleName,
                                lifespan = selectedLifespan,
                                lastChangedTimestamp = maintenanceTimestamp
                            )
                            addingCandleFilter = null
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { addingCandleFilter = null }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    renamingCandle?.let { candle ->
        var newName by remember { mutableStateOf(candle.name) }
        AlertDialog(
            onDismissRequest = { renamingCandle = null },
            title = { Text(text = "تعديل اسم الشمعة") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("اسم الشمعة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.renameCandle(candle, newName)
                            renamingCandle = null
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { renamingCandle = null }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    deletingCandle?.let { candle ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { deletingCandle = null },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Red Trash Icon in Soft Red Circle
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Title
                Text(
                    text = stringResource(R.string.delete_candle_confirm_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D2823)
                    ),
                    textAlign = TextAlign.Center
                )

                // Description Message
                Text(
                    text = stringResource(R.string.delete_candle_confirm_message, candle.name),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF6A8B83)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Red Confirm Button
                Button(
                    onClick = {
                        viewModel.deleteCandle(candle)
                        deletingCandle = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.yes_delete_candle),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Secondary Soft Gray Cancel Button
                Button(
                    onClick = { deletingCandle = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2F4F5),
                        contentColor = Color(0xFF2C3E50)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.cancel_action),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreenContent(
    filters: List<FilterWithCandles>,
    selectedFilterId: Long?,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onFilterSelected: (Long) -> Unit,
    onAddFilter: () -> Unit,
    onDeleteFilter: (Filter) -> Unit,
    onMarkAsChanged: (Candle) -> Unit,
    onEditDate: (Candle) -> Unit,
    onRenameCandle: (Candle) -> Unit,
    onEditLifespan: (Candle) -> Unit = {},
    onAddCandleToFilter: (FilterWithCandles) -> Unit = {},
    onDeleteCandle: (Candle) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showFilterDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Permission Check (Android 13+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
    
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
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                            Text(
                                text = currentFilter?.filter?.name ?: stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )

                    }
                },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        Surface(
                            onClick = { /* notification action */ },
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = stringResource(R.string.settings),
                                    tint = Color(0xFF4A6B63),
                                    modifier = Modifier.size(20.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopStart)
                                        .padding(top = 2.dp, start = 2.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE53935))
                                )
                            }
                        }
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        Surface(
                            onClick = { if (currentFilter != null) showMenu = true },
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = stringResource(R.string.settings),
                                    tint = Color(0xFF4A6B63),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (currentFilter != null) {
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
                icon = { Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.White) },
                text = {
                    Text(
                        text = stringResource(R.string.add_filter),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                shape = CircleShape,
                containerColor = Color(0xFF00BFA5)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Permission Warning (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                if (!permissionState.status.isGranted) {
                    PermissionWarningCard(
                        onAction = {
                            if (permissionState.status.shouldShowRationale) {
                                permissionState.launchPermissionRequest()
                            } else {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }
                        },
                        buttonText = stringResource(
                            if (permissionState.status.shouldShowRationale) R.string.ok 
                            else R.string.open_settings
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filters.isEmpty()) {
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
                                    imageVector = Icons.Rounded.WaterDrop,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.no_filters),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.add_filter_desc),
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onAddFilter,
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.add_filter))
                        }
                    }
                }
            } else {
                // Filter Selector (Modern Chips)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(filters) { index, filterWithCandles ->
                        val isSelected = selectedFilterIndex == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterSelected(filterWithCandles.filter.id) },
                            label = { 
                                Text(
                                    text = filterWithCandles.filter.name,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelLarge
                                ) 
                            },
                            modifier = Modifier.widthIn(min = 80.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }

                currentFilter?.let {
                    Box(modifier = Modifier.weight(1f)) {
                        DashboardContent(
                            filter = it,
                            onMarkAsChanged = onMarkAsChanged,
                            onEditDate = onEditDate,
                            onRenameCandle = onRenameCandle,
                            onEditLifespan = onEditLifespan,
                            onAddCandle = { onAddCandleToFilter(it) },
                            onDeleteCandle = onDeleteCandle
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun DashboardScreenPreview() {
    WaterFilterTheme {
        DashboardScreenContent(
            filters = listOf(
                FilterWithCandles(
                    filter = Filter(1L, "Home", FilterType.CANDLES_3),
                    candles = emptyList()
                ),
                FilterWithCandles(
                    filter = Filter(2L, "Office", FilterType.CANDLES_3),
                    candles = emptyList()
                )
            ),
            selectedFilterId = 1L,
            isLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onFilterSelected = {},
            onAddFilter = {},
            onDeleteFilter = {},
            onMarkAsChanged = {},
            onEditDate = {},
            onRenameCandle = {}
        )
    }
}
