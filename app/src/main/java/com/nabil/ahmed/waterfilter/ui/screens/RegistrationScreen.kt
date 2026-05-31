package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabil.ahmed.waterfilter.R
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNext: (name: String, stages: Int) -> Unit,
    onBack: () -> Unit
) {
    var filterName by remember { mutableStateOf("") }
    var selectedStages by remember { mutableIntStateOf(3) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
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
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = stringResource(R.string.cancel))
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
                    onClick = { if (filterName.isNotBlank()) onNext(filterName, selectedStages) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .navigationBarsPadding()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = filterName.isNotBlank()
                ) {
                    Text(
                        text = stringResource(R.string.next),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.add_new_filter),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.add_filter_desc),
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.filter_name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = filterName,
                onValueChange = { filterName = it },
                placeholder = { Text(stringResource(R.string.filter_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.filter_type),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StageOption(
                    title = stringResource(R.string.stages_3),
                    isSelected = selectedStages == 3,
                    onClick = { selectedStages = 3 },
                    modifier = Modifier.weight(1f)
                )
                StageOption(
                    title = stringResource(R.string.stages_5),
                    isSelected = selectedStages == 5,
                    onClick = { selectedStages = 5 },
                    modifier = Modifier.weight(1f)
                )
                StageOption(
                    title = stringResource(R.string.stages_7),
                    isSelected = selectedStages == 7,
                    onClick = { selectedStages = 7 },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun RegistrationPreview() {
    WaterFilterTheme {
        RegistrationScreen(onNext = { _, _ -> }, onBack = {})
    }
}
