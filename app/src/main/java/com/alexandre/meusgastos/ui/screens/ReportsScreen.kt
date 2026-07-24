package com.alexandre.meusgastos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Duas visões: (1) barras com o total gasto por dia no período — mostra picos e tendência;
 * (2) lista por categoria com participação percentual — mais legível em telas de celular
 * do que um gráfico de pizza denso com 8+ fatias.
 */
@Composable
fun ReportsScreen(viewModel: ExpenseViewModel) {
    var periodStart by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var periodEnd by remember { mutableStateOf(LocalDate.now()) }

    val dailyTotals by viewModel.dailyTotals(periodStart, periodEnd).collectAsState()
    val categoryTotals by viewModel.categoryTotals(periodStart, periodEnd).collectAsState()

    val chartModelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(dailyTotals) {
        chartModelProducer.tryRunTransaction {
            columnSeries { series(dailyTotals.map { it.total }) }
        }
    }

    val grandTotal = categoryTotals.sumOf { it.total }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Relatórios", style = MaterialTheme.typography.headlineSmall)
        Text(
            "${periodStart.format(DateTimeFormatter.ofPattern("dd/MM"))} a " +
                periodEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            style = MaterialTheme.typography.bodyMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Evolução diária", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(),
                    ),
                    modelProducer = chartModelProducer,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Por categoria", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                categoryTotals.filter { it.total > 0 }.forEach { catTotal ->
                    val percent = if (grandTotal > 0) (catTotal.total / grandTotal * 100) else 0.0
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(catTotal.categoryName ?: "Sem categoria")
                        Text("R$ %.2f (%.0f%%)".format(catTotal.total, percent))
                    }
                    LinearProgressIndicator(
                        progress = { (percent / 100).toFloat() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
