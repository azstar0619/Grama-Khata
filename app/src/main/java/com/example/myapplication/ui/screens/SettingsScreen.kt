package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.Language
import com.example.myapplication.data.LocalStrings
import com.example.myapplication.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val isSyncing by viewModel.isSyncing.collectAsState()
    val language by viewModel.language.collectAsState()
    val shopName by viewModel.shopName.collectAsState()

    var editingShopName by remember { mutableStateOf(false) }
    var shopNameDraft by remember(shopName) { mutableStateOf(shopName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settings, fontWeight = FontWeight.Bold) },
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
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Shop Name ────────────────────────────────────────────────────
            ListItem(
                headlineContent = { Text(strings.shopName, fontWeight = FontWeight.Medium) },
                supportingContent = {
                    if (editingShopName) {
                        OutlinedTextField(
                            value = shopNameDraft,
                            onValueChange = { shopNameDraft = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    } else {
                        Text(shopName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                leadingContent = { Icon(Icons.Default.Store, contentDescription = null) },
                trailingContent = {
                    if (editingShopName) {
                        Row {
                            TextButton(onClick = { editingShopName = false }) { Text("Cancel") }
                            TextButton(onClick = {
                                viewModel.updateShopName(shopNameDraft.trim().ifBlank { shopName })
                                editingShopName = false
                            }) { Text("Save") }
                        }
                    } else {
                        IconButton(onClick = { editingShopName = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )

            HorizontalDivider()

            // ── Language ─────────────────────────────────────────────────────
            ListItem(
                headlineContent = { Text("Language / ಭಾಷೆ", fontWeight = FontWeight.Medium) },
                supportingContent = {
                    Text(if (language == Language.ENGLISH) "English" else "ಕನ್ನಡ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                leadingContent = { Icon(Icons.Default.Language, contentDescription = null) },
                trailingContent = {
                    Switch(
                        checked = language == Language.KANNADA,
                        onCheckedChange = { viewModel.toggleLanguage() }
                    )
                }
            )

            HorizontalDivider()

            // ── Sync ─────────────────────────────────────────────────────────
            ListItem(
                headlineContent = { Text(strings.syncNow, fontWeight = FontWeight.Medium) },
                supportingContent = {
                    Text(if (isSyncing) strings.syncing else "${strings.lastSynced}: just now",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                leadingContent = {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null)
                    }
                },
                trailingContent = {
                    FilledTonalButton(
                        onClick = { viewModel.sync() },
                        enabled = !isSyncing
                    ) { Text(strings.syncNow) }
                }
            )

            HorizontalDivider()

            Spacer(Modifier.weight(1f))

            // ── Version footer ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.appName, style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(strings.version, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline)
                    Text(strings.tagline, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
