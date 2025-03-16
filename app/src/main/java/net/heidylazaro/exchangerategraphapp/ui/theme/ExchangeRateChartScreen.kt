package net.heidylazaro.exchangerategraphapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import net.heidylazaro.exchangerategraphapp.model.ExchangeRate

@Composable
fun ExchangeRateChartScreen(
    exchangeRates: List<ExchangeRate>,
    onLoadData: (String, String, String) -> Unit
) {
    var currency by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(text = "Moneda",
            modifier = Modifier.padding(top = 30.dp))
        TextField(
            value = currency.uppercase(),
            onValueChange = { currency = it.uppercase() },
            placeholder = { Text("USD") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Fecha Inicial (YYYY-MM-DD)")
        TextField(
            value = startDate,
            onValueChange = { startDate = it },
            placeholder = { Text("Ej: 2025-01-01") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Fecha Final (YYYY-MM-DD)")
        TextField(
            value = endDate,
            onValueChange = { endDate = it },
            placeholder = { Text("Ej: 2025-12-31") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onLoadData(currency, startDate, endDate) },
            enabled = currency.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty(),
            modifier = Modifier.padding(top = 8.dp)) {
            Text("Cargar Datos")
        }

        Spacer(modifier = Modifier.height(16.dp))

       ExchangeRateGraph(
        exchangeRates = exchangeRates,
        currency = currency,
        startDate = startDate,
        endDate = endDate
        )
    }
}

@Composable
fun ExchangeRateGraph(
    exchangeRates: List<ExchangeRate>,
    currency: String,
    startDate: String,
    endDate: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Resumen de la información
        Text(
            text = "Moneda: $currency",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Fecha Inicial: $startDate",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = "Fecha Final: $endDate",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Mostrar los valores
        if (exchangeRates.isNotEmpty()) {
            Text(
                text = "Total de valores: ${exchangeRates.size}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        } else {
            Text("No hay datos para mostrar.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aquí la gráfica original
        val maxRate = exchangeRates.maxOfOrNull { it.rate }?.toFloat() ?: 0f
        val minRate = exchangeRates.minOfOrNull { it.rate }?.toFloat() ?: 0f

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) { // Altura limitada para no ocupar toda la pantalla

            val canvasWidth = size.width
            val canvasHeight = size.height

            // Dibujar las líneas del gráfico
            drawLine(
                start = Offset(50f, 0f),
                end = Offset(50f, canvasHeight),
                color = Color.Black,
                strokeWidth = 2f
            )
            drawLine(
                start = Offset(0f, canvasHeight - 50f),
                end = Offset(canvasWidth, canvasHeight - 50f),
                color = Color.Black,
                strokeWidth = 2f
            )

            if (exchangeRates.size > 1 && maxRate != minRate) {
                val scaleX = (canvasWidth - 100f) / (exchangeRates.size - 1)
                val scaleY = (canvasHeight - 100f) / (maxRate - minRate)

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

                // Dibuja los puntos
                exchangeRates.forEachIndexed { index, exchangeRate ->
                    val x = 50f + index * scaleX
                    val y = canvasHeight - 50f - ((exchangeRate.rate - minRate).toFloat()) * scaleY

                    drawCircle(
                        color = Color.Red,
                        radius = 5f,
                        center = Offset(x, y)
                    )
                }

                // Mostrar los valores mínimo y máximo en el eje Y
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    // Dibujar el valor mínimo en la parte inferior izquierda
                    drawText(
                        String.format("%.2f", minRate),
                        50f, // Posición en X para el valor mínimo
                        canvasHeight - 50f, // Posición en Y para el valor mínimo (abajo)
                        textPaint
                    )

                    // Dibujar el valor máximo en la parte superior izquierda
                    drawText(
                        String.format("%.2f", maxRate),
                        50f, // Posición en X para el valor máximo
                        50f, // Posición en Y para el valor máximo (arriba)
                        textPaint
                    )
                }
            }
        }
    }
}
