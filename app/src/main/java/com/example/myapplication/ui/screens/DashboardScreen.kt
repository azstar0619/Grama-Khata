package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.components.CustomerAvatar
import com.example.myapplication.ui.components.formatTimestamp
import com.example.myapplication.ui.theme.AmountAdvance
import com.example.myapplication.ui.theme.AmountDue
import com.example.myapplication.ui.theme.AmountNeutral
import com.example.myapplication.ui.theme.GreenDark
import com.example.myapplication.ui.theme.GreenMid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val customers by viewModel.customers.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val todayCollection by viewModel.todayCollection.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val shopName by viewModel.shopName.collectAsState()
    val language by viewModel.language.collectAsState()

    val totalOutstanding = customers.filter { it.balance > 0 }.sumOf { it.balance }
    val peopleWhoOwe = customers.count { it.balance > 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.appName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(shopName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.sync() }) {
                            Icon(Icons.Default.Sync, contentDescription = "Sync")
                        }
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("customers") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(strings.newTransaction) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── Total Outstanding Hero Card ──────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(GreenMid, GreenDark))
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = strings.totalOutstanding,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "₹${"%,.0f".format(totalOutstanding)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "$peopleWhoOwe ${strings.peopleWhoOwe}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            // ── Stat Cards ───────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.People,
                        value = customers.size.toString(),
                        label = strings.customers
                    )
                    DashStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Payments,
                        value = "₹${"%,.0f".format(todayCollection)}",
                        label = strings.todayCollection
                    )
                }
            }

            // ── Quick actions ────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("add_customer") },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.newCustomer, style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = { navController.navigate("dues") },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.sendReminders, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // ── Recent Activity ──────────────────────────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = strings.recentActivity,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (customers.isEmpty()) {
                item {
                    EmptyStateCard(
                        message = strings.noCustomers,
                        icon = Icons.Default.PeopleAlt
                    )
                }
            } else {
                items(customers.take(7)) { customer ->
                    val displayName = if (language == Language.KANNADA) customer.nameKannada else customer.name
                    RecentCustomerRow(
                        customer = customer,
                        displayName = displayName,
                        onClick = { navController.navigate("customer/${customer.id}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashStatCard(modifier: Modifier, icon: ImageVector, value: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun RecentCustomerRow(customer: com.example.myapplication.data.Customer, displayName: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        },
        supportingContent = {
            Text(customer.village, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                val color = if (customer.balance > 0) AmountDue
                    else if (customer.balance < 0) AmountAdvance
                    else AmountNeutral
                Text(
                    text = "₹${"%,.0f".format(kotlin.math.abs(customer.balance))}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = if (customer.balance > 0) "due" else if (customer.balance < 0) "advance" else "settled",
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.8f)
                )
            }
        },
        leadingContent = { CustomerAvatar(name = displayName, size = 44.dp) }
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp), thickness = 0.5.dp)
}

@Composable
fun EmptyStateCard(message: String, icon: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
