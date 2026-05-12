package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.components.CustomerAvatar
import com.example.myapplication.ui.theme.AmountAdvance
import com.example.myapplication.ui.theme.AmountDue
import com.example.myapplication.ui.theme.GreenMid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    viewModel: MainViewModel,
    customerId: String,
    initialType: String
) {
    val strings = LocalStrings.current
    val language by viewModel.language.collectAsState()
    val customer by viewModel.getCustomer(customerId).collectAsState(null)
    val snackbarHostState = remember { SnackbarHostState() }

    var txType by remember { mutableStateOf(
        if (initialType == "PAYMENT") TransactionType.PAYMENT else TransactionType.CREDIT
    ) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val displayName = customer?.let {
        if (language == Language.KANNADA) it.nameKannada else it.name
    } ?: "…"

    val isCredit = txType == TransactionType.CREDIT
    val typeColor = if (isCredit) AmountDue else AmountAdvance
    val amount = amountText.toDoubleOrNull() ?: 0.0

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(strings.newTransaction, fontWeight = FontWeight.Bold)
                        Text(displayName, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer chip
            customer?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomerAvatar(name = displayName, size = 40.dp)
                        Column {
                            Text(displayName, fontWeight = FontWeight.SemiBold)
                            Text(it.phoneNumber, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.weight(1f))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Current",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val col = if (it.balance > 0) AmountDue else AmountAdvance
                            Text("₹${"%,.0f".format(kotlin.math.abs(it.balance))}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold, color = col)
                        }
                    }
                }
            }

            // Transaction Type Selector
            Text("Transaction Type", style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Credit button
                val creditSelected = isCredit
                Button(
                    onClick = { txType = TransactionType.CREDIT },
                    modifier = Modifier.weight(1f).height(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (creditSelected) AmountDue else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (creditSelected) Color.White else AmountDue
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.height(2.dp))
                        Text(strings.credit, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                        Text(strings.creditLabel, style = MaterialTheme.typography.labelSmall)
                    }
                }
                // Payment button
                val paymentSelected = !isCredit
                Button(
                    onClick = { txType = TransactionType.PAYMENT },
                    modifier = Modifier.weight(1f).height(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (paymentSelected) GreenMid else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (paymentSelected) Color.White else GreenMid
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.height(2.dp))
                        Text(strings.payment, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                        Text(strings.paymentLabel, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Amount Field
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it.filter { c -> c.isDigit() || c == '.' }
                    amountError = false
                },
                label = { Text(strings.amount) },
                modifier = Modifier.fillMaxWidth(),
                isError = amountError,
                supportingText = if (amountError) {{ Text("Enter a valid amount") }} else null,
                leadingIcon = {
                    Text("₹", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = typeColor,
                        modifier = Modifier.padding(start = 12.dp))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, color = typeColor
                ),
                placeholder = { Text(strings.enterAmount, style = MaterialTheme.typography.bodyLarge) }
            )

            // Quick amount chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(100, 200, 500, 1000).forEach { preset ->
                    SuggestionChip(
                        onClick = { amountText = preset.toString() },
                        label = { Text("₹$preset") }
                    )
                }
            }

            // Notes
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(strings.notes) },
                placeholder = { Text(strings.notesHint) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) }
            )

            Spacer(Modifier.height(4.dp))

            // Save button
            Button(
                onClick = {
                    if (amount <= 0) { amountError = true; return@Button }
                    saving = true
                    viewModel.addTransaction(
                        Transaction(
                            customerId = customerId,
                            amount = amount,
                            type = txType,
                            note = note.trim()
                        )
                    ) {
                        saving = false
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !saving,
                colors = ButtonDefaults.buttonColors(containerColor = typeColor),
                shape = MaterialTheme.shapes.medium
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(strings.save, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
