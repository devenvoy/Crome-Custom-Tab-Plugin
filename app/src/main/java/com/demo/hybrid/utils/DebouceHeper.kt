package com.demo.hybrid.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ============================================
// 1. BUTTON CLICK DEBOUNCE (StateFlow)
// ============================================
class ButtonDebouncer(
    private val scope: CoroutineScope,
    private val delayMs: Long = 500L
) {
    private val _clicks = MutableStateFlow(0L)

    val debouncedClicks: StateFlow<Long> = _clicks
        .debounce(delayMs)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    fun onClick() {
        _clicks.value = System.currentTimeMillis()
    }
}

// ============================================
// 2. SEARCH QUERY DEBOUNCE (StateFlow)
// ============================================
class SearchDebouncer(
    private val scope: CoroutineScope,
    private val delayMs: Long = 300L
) {
    private val _searchQuery = MutableStateFlow("")

    val debouncedQuery: StateFlow<String> = _searchQuery
        .debounce(delayMs)
        .distinctUntilChanged()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clear() {
        _searchQuery.value = ""
    }
}

// ============================================
// 3. REUSABLE GENERIC DEBOUNCE (StateFlow)
// ============================================
class Debouncer<T>(
    private val scope: CoroutineScope,
    private val delayMs: Long = 500L,
    initialValue: T
) {
    private val _value = MutableStateFlow(initialValue)

    val debouncedValue: StateFlow<T> = _value
        .debounce(delayMs)
        .distinctUntilChanged()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialValue
        )

    fun emit(value: T) {
        _value.value = value
    }

    val currentValue: T
        get() = _value.value
}

// ============================================
// COMPOSABLE DEBOUNCE CONTROLLERS
// ============================================

/**
 * Debounce controller for button clicks
 */
@Composable
fun rememberButtonDebounce(
    delayMs: Long = 500L,
    onDebounced: () -> Unit
): () -> Unit {
    val scope = rememberCoroutineScope()
    val debouncer = remember(delayMs) {
        ButtonDebouncer(scope, delayMs)
    }

    LaunchedEffect(Unit) {
        debouncer.debouncedClicks
            .drop(1) // Skip initial value
            .collect {
                onDebounced()
            }
    }

    return remember { { debouncer.onClick() } }
}

/**
 * Debounce controller for search queries
 */
@Composable
fun rememberSearchDebounce(
    delayMs: Long = 300L,
    onDebounced: (String) -> Unit
): (String) -> Unit {
    val scope = rememberCoroutineScope()
    val debouncer = remember(delayMs) {
        SearchDebouncer(scope, delayMs)
    }

    LaunchedEffect(Unit) {
        debouncer.debouncedQuery.collect { query ->
            onDebounced(query)
        }
    }

    return remember { { query: String -> debouncer.onQueryChange(query) } }
}

/**
 * Generic reusable debounce controller
 */
@Composable
fun <T> rememberDebounce(
    initialValue: T,
    delayMs: Long = 500L,
    onDebounced: (T) -> Unit
): (T) -> Unit {
    val scope = rememberCoroutineScope()
    val debouncer = remember(delayMs) {
        Debouncer(scope, delayMs, initialValue)
    }

    LaunchedEffect(Unit) {
        debouncer.debouncedValue
            .drop(1) // Skip initial value
            .collect { value ->
                onDebounced(value)
            }
    }

    return remember { { value: T -> debouncer.emit(value) } }
}

// ============================================
// EXAMPLE USAGE
// ============================================

@Composable
fun DebouncedButtonExample() {
    var clickCount by remember { mutableStateOf(0) }
    var debouncedCount by remember { mutableStateOf(0) }

    val handleDebouncedClick = rememberButtonDebounce(
        delayMs = 500L,
        onDebounced = {
            debouncedCount++
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            clickCount++
            handleDebouncedClick()
        }) {
            Text("Click Me")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Immediate clicks: $clickCount")
        Text("Debounced clicks: $debouncedCount")
    }
}

@Composable
fun DebouncedSearchExample() {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }

    val handleSearchDebounce = rememberSearchDebounce(
        delayMs = 300L,
        onDebounced = { query ->
            // Simulate search
            searchResults = if (query.isNotEmpty()) {
                listOf("Result for: $query", "Another result", "More results...")
            } else {
                emptyList()
            }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                handleSearchDebounce(it)
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        searchResults.forEach { result ->
            Text(result, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun GenericDebounceExample() {
    var sliderValue by remember { mutableStateOf(0f) }
    var debouncedValue by remember { mutableStateOf(0f) }

    val handleDebounce = rememberDebounce(
        initialValue = 0f,
        delayMs = 500L,
        onDebounced = { value ->
            debouncedValue = value
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Immediate: ${sliderValue.toInt()}")
        Text("Debounced: ${debouncedValue.toInt()}")

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                handleDebounce(it)
            },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ============================================
// VIEWMODEL USAGE EXAMPLE
// ============================================

class MyViewModel(private val scope: CoroutineScope) {
    // Button debounce
    private val buttonDebouncer = ButtonDebouncer(scope, 500L)

    init {
        scope.launch {
            buttonDebouncer.debouncedClicks.collect { timestamp ->
                // Handle debounced button click
                println("Debounced click at: $timestamp")
            }
        }
    }

    fun onButtonClick() = buttonDebouncer.onClick()

    // Search debounce
    private val searchDebouncer = SearchDebouncer(scope, 300L)

    init {
        scope.launch {
            searchDebouncer.debouncedQuery.collect { query ->
                // Perform search
                performSearch(query)
            }
        }
    }

    fun onSearchQueryChange(query: String) = searchDebouncer.onQueryChange(query)

    private fun performSearch(query: String) {
        println("Searching for: $query")
    }

    // Generic debounce for filter
    private val filterDebouncer = Debouncer(scope, 400L, "")

    init {
        scope.launch {
            filterDebouncer.debouncedValue.collect { filter ->
                // Apply filter
                println("Applying filter: $filter")
            }
        }
    }

    fun onFilterChange(filter: String) = filterDebouncer.emit(filter)
}