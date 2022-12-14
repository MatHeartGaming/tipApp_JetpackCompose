package it.spaarkly.jettipapp.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val IconbuttonSizeModifier = Modifier.size(40.dp)

@Composable
fun RoundIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onClickAction: ()-> Unit,
    tint : Color = Color.Black.copy(alpha = 0.8f),
    backgroundColor : Color = MaterialTheme.colors.background,
    elevation: Dp = 4.dp
) {
    Card(modifier = modifier.clip(CircleShape)
        .padding(4.dp)
        .clickable { onClickAction.invoke() }
        .then(IconbuttonSizeModifier),
        shape = CircleShape,
        backgroundColor = backgroundColor,
        elevation = elevation,
        ) {
        Icon(imageVector = imageVector,
            contentDescription = "Plus or minus Button",
            tint = tint,
        )
    }
}