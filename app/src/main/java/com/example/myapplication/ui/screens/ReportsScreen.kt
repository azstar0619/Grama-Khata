package com.example.myapplication.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.components.CustomerAvatar
import com.example.myapplication.ui.theme.AmountAdvance
import com.example.myapplication.ui.theme.AmountDue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ReportPeriod(val label: String) {
    TODAY("Today"),
    WEEK("This Week"),
    MONTH("This Month")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController, viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val customers by viewModel.customers.collectAsState()
    val shopName by viewModel.shopName.collectAsState()
    val language by viewModel.language.collectAsState()
    val context = LocalContext.current

    var selectedPeriod by remember { mutableStateOf(ReportPeriod.TODAY) }

    val dueCustomers = remember(customers) { customers.filter { it.balance > 0 }.sortedByDescending { it.balance } }
    val advanceCustomers = remember(customers) { customers.filter { it.balance < 0 }.sortedBy { it.balance } }
    val totalOutstanding = remember(customers) { customers.filter { it.balance > 0 }.sumOf { it.balance } }
    val totalAdvance = remember(customers) { customers.filter { it.balance < 0 }.sumOf { it.balance } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.reports, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {
                        val report = buildReport(customers, shopName)
                        context.startActivity(
                            Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, report)
                            }, "Share Report")
                        )
                    }) {
                        Icon(Icons.Default.Share, contentDescription = strings.shareReport,
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Period Tabs ──────────────────────────────────────────────────
            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    ReportPeriod.entries.forEachIndexed { index, period ->
                        SegmentedButton(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            shape = SegmentedButtonDefaults.itemShape(index, ReportPeriod.entries.size),
                            label = { Text(period.label, style = MaterialTheme.typography.labelLarge) }
                        )
                    }
                }
            }

            // ── Summary Cards ────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.TrendingUp,
                        iconTint = AmountDue,
                        label = "Outstanding",
                        value = "₹${"%,.0f".format(totalOutstanding)}",
                        valueColor = AmountDue
                    )
                    ReportStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.TrendingDown,
                        iconTint = AmountAdvance,
                        label = "Advance",
                        value = "₹${"%,.0f".format(kotlin.math.abs(totalAdvance))}",
                        valueColor = AmountAdvance
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReportStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.People,
                        iconTint = MaterialTheme.colorScheme.primary,
                        label = "Total Customers",
                        value = customers.size.toString(),
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                    ReportStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Notifications,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        label = "Pending Dues",
                        value = dueCustomers.size.toString(),
                        valueColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // ── Highest Dues section ─────────────────────────────────────────
            if (dueCustomers.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Highest Dues",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                items(dueCustomers.take(5)) { customer ->
                    val displayName = if (language == Language.KANNADA) customer.nameKannada else customer.name
                    val displayVillage = if (language == Language.KANNADA) customer.villageKannada else customer.village
                    ListItem(
                        headlineContent = {
                            Text(displayName, fontWeight = FontWeight.Medium)
                        },
                        supportingContent = {
                            Text(displayVillage, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        trailingContent = {
                            Text("₹${"%,.0f".format(customer.balance)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold, color = AmountDue)
                        },
                        leadingContent = { CustomerAvatar(name = displayName, size = 40.dp) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 72.dp), thickness = 0.5.dp)
                }
            }

            // ── Share CTA ────────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        val report = buildReport(customers, shopName)
                        context.startActivity(
                            Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, report)
                            }, "Share Report")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.shareReport, style = MaterialTheme.typography.titleSmall)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ReportStatCard(
    modifier: Modifier,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = iconTint.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = valueColor)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun buildReport(customers: List<com.example.myapplication.data.Customer>, shopName: String): String {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    val sb = StringBuilder()
    sb.appendLine("*$shopName — Report*")
    sb.appendLine("Date: $date")
    sb.appendLine()
    val due = customers.filter { it.balance > 0 }.sortedByDescending { it.balance }
    if (due.isNotEmpty()) {
        sb.appendLine("*Pending Dues (${due.size} customers)*")
        due.forEach { sb.appendLine("• ${it.name} (${it.village}): ₹${"%,.0f".format(it.balance)}") }
        sb.appendLine()
        sb.appendLine("*Total Outstanding: ₹${"%,.0f".format(due.sumOf { it.balance })}*")
    } else {
        sb.appendLine("No pending dues! All settled.")
    }
    return sb.toString()
}
