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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.Customer
import com.example.myapplication.data.LocalStrings
import com.example.myapplication.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(navController: NavController, viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var nameKn by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var villageKn by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError = name.isBlank()
        phoneError = phone.isBlank() || phone.replace(Regex("[^0-9]"), "").length < 10
        return !nameError && !phoneError
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(strings.addCustomer, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Customer Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text(strings.customerName) },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = if (nameError) {{ Text("Name is required") }} else null,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            OutlinedTextField(
                value = nameKn,
                onValueChange = { nameKn = it },
                label = { Text(strings.customerNameKn) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null) },
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it; phoneError = false },
                label = { Text(strings.phone) },
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError,
                supportingText = if (phoneError) {{ Text("Enter valid 10-digit number") }} else null,
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("Location",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text(strings.village) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            OutlinedTextField(
                value = villageKn,
                onValueChange = { villageKn = it },
                label = { Text(strings.villageKn) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null) },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button
                    saving = true
                    viewModel.addCustomer(
                        Customer(
                            name = name.trim(),
                            nameKannada = nameKn.trim().ifBlank { name.trim() },
                            phoneNumber = phone.trim(),
                            village = village.trim().ifBlank { "—" },
                            villageKannada = villageKn.trim().ifBlank { village.trim().ifBlank { "—" } }
                        )
                    ) {
                        saving = false
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !saving,
                shape = MaterialTheme.shapes.medium
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                }
                Text(strings.save, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
