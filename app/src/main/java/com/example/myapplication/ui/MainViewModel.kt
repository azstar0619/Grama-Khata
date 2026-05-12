package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import com.example.myapplication.data.db.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GramaKhataRepository(AppDatabase.getDatabase(application))

    val customers: StateFlow<List<Customer>> = repository.customers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<Transaction>> = repository.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayCollection = MutableStateFlow(0.0)
    val todayCollection: StateFlow<Double> = _todayCollection.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _language = MutableStateFlow(Language.ENGLISH)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _shopName = MutableStateFlow("Shri Kirana Store")
    val shopName: StateFlow<String> = _shopName.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            refreshTodayCollection()
        }
    }

    fun getCustomer(id: String): Flow<Customer?> = repository.getCustomer(id)

    fun getTransactionsForCustomer(customerId: String): Flow<List<Transaction>> =
        repository.getTransactionsForCustomer(customerId)

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.ENGLISH) Language.KANNADA else Language.ENGLISH
    }

    fun updateShopName(name: String) {
        _shopName.value = name
    }

    fun addCustomer(customer: Customer, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addCustomer(customer)
            onDone()
        }
    }

    fun addTransaction(transaction: Transaction, onDone: () -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.addTransaction(transaction)
            refreshTodayCollection()
            delay(400)
            _isSyncing.value = false
            onDone()
        }
    }

    fun sync() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1500)
            _isSyncing.value = false
        }
    }

    private suspend fun refreshTodayCollection() {
        _todayCollection.value = repository.getTodayCollection()
    }
}
