package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.components.CustomerAvatar
import com.example.myapplication.ui.components.daysSince
import com.example.myapplication.ui.theme.AmountDue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuesScreen(navController: NavController, viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val customers by viewModel.customers.collectAsState()
    val language by viewModel.language.collectAsState()
    val shopName by viewModel.shopName.collectAsState()
    val context = LocalContext.current

    val dueCustomers = remember(customers) {
        customers.filter { it.balance > 0 }.sortedByDescending { it.balance }
    }

    val totalDue = dueCustomers.sumOf { it.balance }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.pendingReminders, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (dueCustomers.isNotEmpty()) {
                        TextButton(
                            onClick = { sendBulkReminders(context, dueCustomers, shopName) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.sendReminderToAll, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (dueCustomers.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text(strings.noDues, style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Summary card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AmountDue.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(strings.totalOutstanding,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "₹${"%,.0f".format(totalDue)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmountDue
                                )
                                Text("from ${dueCustomers.size} customers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.AccountBalance, contentDescription = null,
                                modifier = Modifier.size(40.dp), tint = AmountDue.copy(alpha = 0.4f))
                        }
                    }
                }

                items(dueCustomers, key = { it.id }) { customer ->
                    val displayName = if (language == Language.KANNADA) customer.nameKannada else customer.name
                    val displayVillage = if (language == Language.KANNADA) customer.villageKannada else customer.village
                    val days = daysSince(customer.lastTransactionDate)
                    DueCustomerCard(
                        customer = customer,
                        displayName = displayName,
                        displayVillage = displayVillage,
                        daysSince = days,
                        onOpenDetail = { navController.navigate("customer/${customer.id}") },
                        onSendReminder = { sendSingleReminder(context, customer, displayName, shopName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DueCustomerCard(
    customer: com.example.myapplication.data.Customer,
    displayName: String,
    displayVillage: String,
    daysSince: Long,
    onOpenDetail: () -> Unit,
    onSendReminder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onOpenDetail
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomerAvatar(name = displayName, size = 48.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(displayName, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge)
                Text(displayVillage, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (daysSince > 0) {
                    Text("${daysSince}d pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (daysSince > 7) AmountDue else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${"%,.0f".format(customer.balance)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AmountDue
                )
                Spacer(Modifier.height(4.dp))
                FilledTonalIconButton(
                    onClick = onSendReminder,
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0xFF25D366).copy(alpha = 0.12f),
                        contentColor = Color(0xFF25D366)
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Reminder",
                        modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun sendSingleReminder(context: Context, customer: com.example.myapplication.data.Customer, displayName: String, shopName: String) {
    val message = "Namaskara *$displayName*-avare 🙏\n\n" +
        "*$shopName* dalli nimma baaki:\n" +
        "*₹${"%,.0f".format(customer.balance)}*\n\n" +
        "Dayavittu yavaga baaki tegedukolluveeri?\n\n" +
        "_Dhanyavadagalu_ 🙏"
    val cleanPhone = customer.phoneNumber.replace(Regex("[^0-9]"), "")
    val fullPhone = if (cleanPhone.startsWith("91")) cleanPhone else "91$cleanPhone"
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send?phone=$fullPhone&text=${Uri.encode(message)}"))
        )
    } catch (e: Exception) {
        context.startActivity(
            Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }, "Send Reminder")
        )
    }
}

private fun sendBulkReminders(context: Context, customers: List<com.example.myapplication.data.Customer>, shopName: String) {
    val sb = StringBuilder()
    sb.appendLine("*$shopName — Baaki Pattikaanu (Due List)*\n")
    customers.forEach { sb.appendLine("• ${it.name}: ₹${"%,.0f".format(it.balance)}") }
    sb.appendLine("\n_Dayavittu baaki tegudukolluvalire_ 🙏")
    context.startActivity(
        Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }, "Share Due List")
    )
}
