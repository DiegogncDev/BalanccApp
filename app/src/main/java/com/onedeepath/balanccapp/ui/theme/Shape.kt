package com.onedeepath.balanccapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val BalanccShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

object BalanccCornerRadius {
    val control = 12.dp
    val input = 16.dp
    val card = 20.dp
    val container = 24.dp
    val sheet = 28.dp
    val floatingAction = 20.dp
}
