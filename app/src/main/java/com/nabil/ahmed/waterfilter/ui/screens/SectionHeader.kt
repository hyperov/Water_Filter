package com.nabil.ahmed.waterfilter.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.nabil.ahmed.waterfilter.ui.theme.WaterFilterTheme

@Composable
fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SectionHeaderPreview() {
    WaterFilterTheme {
        SectionHeader(title = "العنوان")
    }
}
