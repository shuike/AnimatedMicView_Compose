package com.shuike.animatedmicview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.shuike.animatedmicview.ui.theme.AnimatedMicViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimatedMicViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val status = remember {
                        mutableStateOf(MicAnimatedViewStatus.IDLE)
                    }
                    Box(
                        Modifier
                            .background(Color.Black)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                status.value = when (status.value) {
                                    MicAnimatedViewStatus.IDLE -> {
                                        MicAnimatedViewStatus.SPEAKING
                                    }

                                    MicAnimatedViewStatus.SPEAKING -> {
                                        MicAnimatedViewStatus.PROCESSING
                                    }

                                    MicAnimatedViewStatus.PROCESSING -> {
                                        MicAnimatedViewStatus.IDLE
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        MicAnimatedView(micViewStatus = status.value)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AnimatedMicViewTheme {
        MicAnimatedView(micViewStatus = MicAnimatedViewStatus.IDLE)
    }
}