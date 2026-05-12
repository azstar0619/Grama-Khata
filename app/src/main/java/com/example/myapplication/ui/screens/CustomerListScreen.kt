package com.example.myapplication.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.components.CustomerAvatar
import com.example.myapplication.ui.theme.AmountAdvance
import com.example.myapplication.ui.theme.AmountDue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(navController: NavController, viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val customers by viewModel.customers.collectAsState()
    val language by viewModel.language.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(customers, searchQuery, language) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.nameKannada.contains(searchQuery, ignoreCase = true) ||
            it.village.contains(searchQuery, ignoreCase = true) ||
            it.villageKannada.contains(searchQuery, ignoreCase = true) ||
            it.phoneNumber.contains(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(strings.customers, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_customer") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = strings.addCustomer)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                placeholder = { Text(strings.search, style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            if (filtered.isEmpty()) {
                EmptyStateCard(
                    message = if (searchQuery.isBlank()) strings.noCustomers else "No results for \"$searchQuery\"",
                    icon = if (searchQuery.isBlank()) Icons.Default.PeopleAlt else Icons.Default.SearchOff
                )
            } else {
                // Summary row
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${filtered.size} customers · sorted by highest due",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filtered, key = { it.id }) { customer ->
                        val displayName = if (language == Language.KANNADA) customer.nameKannada else customer.name
                        val displayVillage = if (language == Language.KANNADA) customer.villageKannada else customer.village
                        CustomerListItem(
                            customer = customer,
                            displayName = displayName,
                            displayVillage = displayVillage,
                            onClick = { navController.navigate("customer/${customer.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerListItem(
    customer: Customer,
    displayName: String,
    displayVillage: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(displayName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(displayVillage, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("· ${customer.phoneNumber.take(7)}****",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                val color = when {
                    customer.balance > 0 -> AmountDue
                    customer.balance < 0 -> AmountAdvance
                    else -> MaterialTheme.colorScheme.outline
                }
                Text(
                    text = "₹${"%,.0f".format(kotlin.math.abs(customer.balance))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                val label = when {
                    customer.balance > 0 -> "owe"
                    customer.balance < 0 -> "advance"
                    else -> "settled"
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color.copy(alpha = 0.12f)
                ) {
                    Text(label, style = MaterialTheme.typography.labelSmall,
                        color = color, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        },
        leadingContent = { CustomerAvatar(name = displayName, size = 48.dp) }
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp), thickness = 0.5.dp)
}
