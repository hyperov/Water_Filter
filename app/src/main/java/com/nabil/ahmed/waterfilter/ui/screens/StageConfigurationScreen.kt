package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.data.util.FilterType
import com.nabil.ahmed.waterfilter.data.util.Lifespan
import com.nabil.ahmed.waterfilter.data.util.StageNumber
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageConfigurationScreen(
    filterName: String,
    filterType: FilterType,
    onSave: (List<StageConfigState>) -> Unit,
    onBack: () -> Unit
) {
    val stages = remember {
        mutableStateListOf<StageConfigState>().apply {
            add(StageConfigState(StageNumber.STAGE_1, R.string.stage_pp, R.string.stage_pp_desc, Lifespan.MONTHS_3))
            add(StageConfigState(StageNumber.STAGE_2, R.string.stage_gac, R.string.stage_gac_desc, Lifespan.MONTHS_6))
            add(StageConfigState(StageNumber.STAGE_3, R.string.stage_cto, R.string.stage_cto_desc, Lifespan.MONTHS_6))
            if (filterType.stagesCount >= 5) {
                add(StageConfigState(StageNumber.STAGE_4, R.string.stage_ro, R.string.stage_ro_desc, Lifespan.MONTHS_24))
                add(StageConfigState(StageNumber.STAGE_5, R.string.stage_post_carbon, R.string.stage_post_carbon_desc, Lifespan.MONTHS_12))
            }
            if (filterType.stagesCount >= 7) {
                add(StageConfigState(StageNumber.STAGE_6, R.string.stage_mineral, R.string.stage_mineral_desc, Lifespan.MONTHS_12))
                add(StageConfigState(StageNumber.STAGE_7, R.string.stage_alkaline, R.string.stage_alkaline_desc, Lifespan.MONTHS_12))
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.stages_history),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp
            ) {
                Button(
                    onClick = { onSave(stages) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .navigationBarsPadding()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.save_and_continue),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.stages_setup),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = stringResource(R.string.step_2_of_2),
                        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.stages_setup_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(stages, key = { it.stageNumber }) { stage ->
                    StageConfigItem(
                        stage = stage,
                        onSelectionChanged = { selection, timestamp ->
                            val index = stages.indexOfFirst { it.stageNumber == stage.stageNumber }
                            if (index != -1) {
                                stages[index] = stage.copy(
                                    selection = selection,
                                    lastChangedTimestamp = timestamp
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StageConfigurationPreview() {
    WaterFilterTheme {
        StageConfigurationScreen(filterName = "Home", filterType = FilterType.STAGES_7, onSave = {}, onBack = {})
    }
}
