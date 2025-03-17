package net.heidylazaro.exchangerategraphapp.ui.theme

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import net.heidylazaro.exchangerategraphapp.model.ExchangeRate
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateChartScreen(
    exchangeRates: List<ExchangeRate>,
    onLoadData: (String, String, String) -> Unit
) {
    var currency by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { Text("Tasas de Cambio") }) }) {
    Column(modifier = Modifier.padding(56.dp)) {
        // Selección de moneda con validación
        Text(text = "Moneda", modifier = Modifier.padding(top = 30.dp))
        TextField(
            value = currency,
            onValueChange = {
                val filteredText = it.uppercase().filter { char -> char.isLetter() }
                if (filteredText.length <= 3) {
                    currency = filteredText
                }
            },
            placeholder = { Text("USD") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para elegir la Fecha Inicial
        Text(text = "Fecha Inicial (YYYY-MM-DD)")
        Button(onClick = {
            showDatePicker(context) { selectedDate ->
                startDate = selectedDate
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (startDate.isEmpty()) "Seleccionar fecha" else startDate)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para elegir la Fecha Final
        Text(text = "Fecha Final (YYYY-MM-DD)")
        Button(onClick = {
            showDatePicker(context) { selectedDate ->
                endDate = selectedDate
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (endDate.isEmpty()) "Seleccionar fecha" else endDate)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cargar los datos
        Button(
            onClick = { onLoadData(currency, startDate, endDate) },
            enabled = currency.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty(),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Cargar Datos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la gráfica
        ExchangeRateGraph(
            exchangeRates = exchangeRates,
            currency = currency,
            startDate = startDate,
            endDate = endDate
        )
    }}
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
        onDateSelected(formattedDate)
    }, year, month, day).show()
}

@Composable
fun ExchangeRateGraph(
    exchangeRates: List<ExchangeRate>,
    currency: String,
    startDate: String,
    endDate: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
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

        if (exchangeRates.isEmpty()) {
            Text("No hay datos para mostrar.")
            return
        }

        Spacer(modifier = Modifier.height(16.dp))

        val maxRate = exchangeRates.maxOf { it.rate }.toFloat()
        val minRate = exchangeRates.minOf { it.rate }.toFloat()
        val safeMax = maxRate + 0.05f
        val safeMin = minRate - 0.05f
        val range = safeMax - safeMin

        // Convertir fechas Unix a formato legible
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val timestamps = exchangeRates.map { it.lastUpdateUnix.toLongOrNull()?.times(1000) ?: 0L }
        val formattedDates = timestamps.map { dateFormat.format(java.util.Date(it)) }

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .background(Color(160,128,160))
            .height(350.dp)) {  // Aumentamos la altura para evitar que las fechas queden pegadas

            val canvasWidth = size.width
            val canvasHeight = size.height

            val scaleX = (canvasWidth - 100f) / (exchangeRates.size - 1).coerceAtLeast(1)
            val scaleY = (canvasHeight - 100f) / range

            // Dibujar los ejes
            drawLine(
                start = Offset(50f, 0f),
                end = Offset(50f, canvasHeight - 50f),
                color = Color.Black,
                strokeWidth = 2f
            )
            drawLine(
                start = Offset(50f, canvasHeight - 50f),
                end = Offset(canvasWidth, canvasHeight - 50f),
                color = Color.Black,
                strokeWidth = 2f
            )

            // Dibujar la gráfica
            for (i in 1 until exchangeRates.size) {
                val prevRate = exchangeRates[i - 1]
                val currentRate = exchangeRates[i]

                val startX = 50f + (i - 1) * scaleX
                val startY = (canvasHeight - 50f - ((prevRate.rate - minRate) * scaleY)).toFloat()

                val stopX = 50f + i * scaleX
                val stopY = (canvasHeight - 50f - ((currentRate.rate - minRate) * scaleY)).toFloat()

                drawLine(
                    start = Offset(startX, startY),
                    end = Offset(stopX, stopY),
                    color = Color.Blue,
                    strokeWidth = 2f
                )
            }

// Dibujar los puntos
            exchangeRates.forEachIndexed { index, exchangeRate ->
                val x = 50f + index * scaleX
                val y = (canvasHeight - 50f - ((exchangeRate.rate - minRate) * scaleY)).toFloat()

                drawCircle(
                    color = Color.Red,
                    radius = 5f,
                    center = Offset(x, y)
                )
            }


            // Dibujar fechas en el eje X con rotación de 45°
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                val step = maxOf(1, exchangeRates.size / 5)  // Mostrar una fecha cada 5 registros aprox.
                for (i in exchangeRates.indices step step) {
                    val x = 50f + i * scaleX
                    val y = canvasHeight - 20f
                    save()
                    rotate(-45f, x, y)  // Rotamos el texto en -45° para mejor legibilidad
                    drawText(formattedDates[i], x, y, textPaint)
                    restore()
                }
            }

            // Dibujar valores en el eje Y
            drawContext.canvas.nativeCanvas.apply {
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }

                val stepY = range / 5
                for (i in 0..5) {
                    val value = minRate + (i * stepY)
                    val y = canvasHeight - 50f - ((value - minRate) * scaleY)
                    drawText(String.format("%.2f", value), 40f, y, textPaint)
                }
            }
        }
    }
}
