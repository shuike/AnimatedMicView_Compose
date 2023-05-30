@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

package com.shuike.animatedmicview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class MicAnimatedViewStatus {
    IDLE, // 空闲中
    SPEAKING, // 讲话中
    PROCESSING, // 处理中
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MicAnimatedView(micViewStatus: MicAnimatedViewStatus) {
    val viewHeight = 35.dp
    val viewWidth = 80.dp

    Box(
        modifier = Modifier
            .height(viewHeight)
            .width(viewWidth),
        contentAlignment = Alignment.Center
    ) {
        // 空闲状态下的图片
        AnimatedVisibility(
            visible = micViewStatus == MicAnimatedViewStatus.IDLE,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_mirco_tabbar),
                contentDescription = "mic"
            )
        }
        // 非空闲状态中中的动画
        AnimatedVisibility(
            visible = micViewStatus != MicAnimatedViewStatus.IDLE,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CanvasAnimatedView(viewHeight, viewWidth, micViewStatus)
        }
    }
}

@Composable
private fun CanvasAnimatedView(
    viewHeight: Dp,
    viewWidth: Dp,
    micViewStatus: MicAnimatedViewStatus,
) {
    val strokeWidth = 3.dp
    val padding = strokeWidth / 2
    val normalWidth = (viewHeight - (padding * 2))
    val speakingWidth = (viewWidth - (padding * 2))
    val rectHeight = (viewHeight - (padding * 2))

    val animateDuration = 200
    val transition = updateTransition(targetState = micViewStatus, label = "")
    val width by transition.animateDp(
        {
            tween(
                durationMillis = animateDuration,
                easing = LinearEasing
            )
        },
        label = "width",
    ) {
        when (it) {
            MicAnimatedViewStatus.SPEAKING -> {
                speakingWidth
            }

            MicAnimatedViewStatus.IDLE,
            MicAnimatedViewStatus.PROCESSING,
            -> {
                normalWidth
            }
        }
    }
    val centerOffSet =
        ((((viewWidth - (padding * 2)) - (viewHeight - (padding * 2))) / 2)).value
    val offSet by transition.animateFloat(
        {
            tween(
                durationMillis = animateDuration,
                easing = LinearEasing
            )
        }, label = "offSet"
    ) {
        when (it) {
            MicAnimatedViewStatus.SPEAKING -> {
                0f
            }

            MicAnimatedViewStatus.IDLE,
            MicAnimatedViewStatus.PROCESSING,
            -> {
                centerOffSet
            }
        }
    }
    val animatedPointAlpha by animateFloatAsState(
        targetValue = if (micViewStatus == MicAnimatedViewStatus.SPEAKING) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing
        )
    )
    val infiniteTransition = rememberInfiniteTransition()
    val pointY by infiniteTransition.animateFloat(
        initialValue = 15.dp.value,
        targetValue = ((viewHeight - (padding * 2)) - 15.dp).value,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val rotateValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        onDraw = {
            drawRoundRect(
                style = Stroke(width = strokeWidth.toPx()),
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFA4E359),
                        Color(0xFFF2DD4A),
                        Color(0xFFA4E359),
                    ),
                ),
                cornerRadius = CornerRadius(
                    drawContext.size.height / 2,
                    drawContext.size.height / 2
                ),
                topLeft = Offset(offSet.dp.toPx(), 0f),
                size = Size(width.toPx(), size.height),
            )

            rotate(rotateValue) {
                drawRoundRect(
                    style = Stroke(width = strokeWidth.toPx()),
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF99E35E),
                            Color(0xFF95E260),
                            Color(0xFFF0D549),
                            Color(0xFFF0DB4A),
                        ),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                    ),
                    cornerRadius = CornerRadius(
                        drawContext.size.height / 2,
                        drawContext.size.height / 2
                    ),
                    topLeft = Offset(centerOffSet.dp.toPx(), 0f),
                    size = Size(normalWidth.toPx(), size.height),
                    alpha = if (micViewStatus == MicAnimatedViewStatus.PROCESSING && width == normalWidth) 1f else 0f
                )
            }
            if (micViewStatus == MicAnimatedViewStatus.SPEAKING) {
                val pointStartX = 18.dp.toPx()
                val pointEndX = size.width - pointStartX
                val fl = (size.width - (pointStartX * 2)) / 4

                val pointCoverY = pointY.dp.toPx()
                val pointY2 = (size.height - padding.value.dp.toPx()) - pointCoverY
                drawPoints(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF99E35E),
                            Color(0xFF95E260),
                            Color(0xFFF0D549),
                            Color(0xFFF0DB4A),
                        ).reversed()
                    ),
                    alpha = animatedPointAlpha,
                    pointMode = PointMode.Points,
                    points = listOf(
                        Offset(pointStartX, pointCoverY),
                        Offset(pointStartX + fl, pointY2),
                        Offset(pointStartX + fl * 2, pointCoverY),
                        Offset(pointStartX + fl * 3, pointY2),
                        Offset(pointEndX, pointCoverY),
                    ),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        })
}

@Preview
@Composable
fun MicViewPreview() {
    MicAnimatedView(MicAnimatedViewStatus.IDLE)
}