package net.heidylazaro.exchangerategraphapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import net.heidylazaro.exchangerategraphapp.model.ExchangeRate

@Composable
fun ExchangeRateChartScreen(
    exchangeRates: List<ExchangeRate>,
    onLoadData: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { onLoadData() }) {
            Text("Cargar Datos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Llamada a la gráfica personalizada
        ExchangeRateGraph(exchangeRates)
    }
}

@Composable
fun ExchangeRateGraph(exchangeRates: List<ExchangeRate>) {
    val maxRate = exchangeRates.maxOfOrNull { it.rate }?.toFloat() ?: 0f
    val minRate = exchangeRates.minOfOrNull { it.rate }?.toFloat() ?: 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Dibuja el eje Y
        drawLine(
            start = Offset(50f, 0f),
            end = Offset(50f, canvasHeight),
            color = Color.Black,
            strokeWidth = 2f
        )

        // Dibuja el eje X
        drawLine(
            start = Offset(0f, canvasHeight - 50f),
            end = Offset(canvasWidth, canvasHeight - 50f),
            color = Color.Black,
            strokeWidth = 2f
        )

        // Escalar las tasas de cambio para ajustarse a la pantalla
        val scaleX = (canvasWidth - 100f) / (exchangeRates.size - 1) // espaciamiento horizontal
        val scaleY = (canvasHeight - 100f) / (maxRate - minRate) // escalado vertical

        // Dibuja la gráfica
        for (i in 1 until exchangeRates.size) {
            val prevRate = exchangeRates[i - 1]
            val currentRate = exchangeRates[i]

            val startX = 50f + (i - 1) * scaleX
            val startY = canvasHeight - 50f - ((prevRate.rate - minRate).toFloat()) * scaleY

            val stopX = 50f + i * scaleX
            val stopY = canvasHeight - 50f - ((currentRate.rate - minRate).toFloat()) * scaleY

            drawLine(
                start = Offset(startX, startY),
                end = Offset(stopX, stopY),
                color = Color.Blue,
                strokeWidth = 2f
            )
        }

        // Dibuja los puntos (si lo deseas)
        exchangeRates.forEachIndexed { index, exchangeRate ->
            val x = 50f + index * scaleX
            val y = canvasHeight - 50f - ((exchangeRate.rate - minRate).toFloat()) * scaleY
            drawCircle(
                color = Color.Red,
                radius = 5f,
                center = Offset(x, y)
            )
        }
    }
}
