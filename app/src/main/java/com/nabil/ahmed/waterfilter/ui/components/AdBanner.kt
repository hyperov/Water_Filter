package com.nabil.ahmed.waterfilter.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.nabil.ahmed.waterfilter.BuildConfig

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = BuildConfig.AD_UNIT_ID
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp), // Banner ads usually have a height of 50dp
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Log.e("AdBanner", "Ad failed to load: ${error.message}, code: ${error.code}")
                    }
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d("AdBanner", "Ad loaded successfully")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
