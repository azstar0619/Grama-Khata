package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.components.CustomerAvatar
import com.example.myapplication.ui.components.formatTimestamp
import com.example.myapplication.ui.theme.AmountAdvance
import com.example.myapplication.ui.theme.AmountDue
import com.example.myapplication.ui.theme.GreenDark
import com.example.myapplication.ui.theme.GreenMid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(navController: NavController, viewModel: MainViewModel, customerId: String) {
    val strings = LocalStrings.current
    val language by viewModel.language.collectAsState()
    val shopName by viewModel.shopName.collectAsState()
    val context = LocalContext.current

    val customer by viewModel.getCustomer(customerId).collectAsState(null)
    val transactions by viewModel.getTransactionsForCustomer(customerId).collectAsState(emptyList())

    if (customer == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val c = customer!!
    val displayName = if (language == Language.KANNADA) c.nameKannada else c.name
    val displayVillage = if (language == Language.KANNADA) c.villageKannada else c.village

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(displayName, fontWeight = FontWeight.Bold)
                        Text(displayVillage, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:${c.phoneNumber}"))
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── Balance Hero ─────────────────────────────────────────────────
            item {
                val balanceColor = if (c.balance > 0) AmountDue else if (c.balance < 0) AmountAdvance
                                   else MaterialTheme.colorScheme.outline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (c.balance > 0)
                                Brush.verticalGradient(listOf(Color(0xFFFFEBEE), Color(0xFFFFF3F3)))
                            else if (c.balance < 0)
                                Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFF4FBF4)))
                            else Brush.verticalGradient(listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface))
                        )
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CustomerAvatar(name = displayName, size = 72.dp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = when {
                                c.balance > 0 -> "$displayName ${strings.customerOwes}"
                                c.balance < 0 -> "${strings.youOwe} $displayName"
                                else -> strings.allSettled
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = balanceColor.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "₹${"%,.0f".format(kotlin.math.abs(c.balance))}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = balanceColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = c.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // ── Action Buttons ───────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("add_transaction/$customerId/CREDIT") },
                        modifier = Modifier.weight(1f).height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmountDue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(strings.credit, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall)
                            Text(strings.creditLabel, style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    Button(
                        onClick = { navController.navigate("add_transaction/$customerId/PAYMENT") },
                        modifier = Modifier.weight(1f).height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenMid,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(strings.payment, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall)
                            Text(strings.paymentLabel, style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // ── WhatsApp Reminder ────────────────────────────────────────────
            item {
                OutlinedButton(
                    onClick = {
                        sendWhatsAppReminder(context, c.phoneNumber, displayName, c.balance, shopName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.sendWhatsApp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Transaction History ──────────────────────────────────────────
            item {
                Text(
                    text = strings.transactionHistory,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (transactions.isEmpty()) {
                item {
                    EmptyStateCard(message = strings.noTransactions, icon = Icons.Default.ReceiptLong)
                }
            } else {
                items(transactions) { tx ->
                    TxRow(tx)
                }
            }
        }
    }
}

@Composable
private fun TxRow(tx: Transaction) {
    val isCredit = tx.type == TransactionType.CREDIT
    val color = if (isCredit) AmountDue else AmountAdvance
    ListItem(
        headlineContent = {
            Text(
                if (isCredit) "Credit — ಕೊಡುವುದು" else "Payment — ತೆಗೆದುಕೊಳ್ಳುವುದು",
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        },
        supportingContent = {
            Column {
                Text(formatTimestamp(tx.timestamp), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (tx.note.isNotBlank()) {
                    Text(tx.note, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        trailingContent = {
            Text(
                "₹${"%,.0f".format(tx.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        },
        leadingContent = {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (isCredit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp), thickness = 0.5.dp)
}

private fun sendWhatsAppReminder(
    context: Context,
    phoneNumber: String,
    customerName: String,
    balance: Double,
    shopName: String
) {
    val message = "Namaskara *$customerName*-avare 🙏\n\n" +
        "*$shopName* dalli nimma baaki:\n" +
        "*₹${"%,.0f".format(kotlin.math.abs(balance))}*\n\n" +
        "Dayavittu yavaga baaki tegedukolluveeri?\n\n" +
        "_Dhanyavadagalu_ 🙏"
    val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
    val fullPhone = if (cleanPhone.startsWith("91")) cleanPhone else "91$cleanPhone"
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send?phone=$fullPhone&text=${Uri.encode(message)}"))
        )
    } catch (e: Exception) {
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                }, "Send Reminder"
            )
        )
    }
}
