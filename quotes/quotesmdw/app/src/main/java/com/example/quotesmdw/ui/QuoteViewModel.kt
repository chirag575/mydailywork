package com.example.quotesmdw.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.quotesmdw.data.Quote
import com.example.quotesmdw.data.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuoteRepository()
    private val prefs = application.getSharedPreferences("quote_prefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
        checkDailyQuote()
    }

    private fun checkDailyQuote() {
        val lastDate = prefs.getString("last_quote_date", "")
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        if (lastDate != currentDate) {
            val newQuote = repository.getRandomQuote()
            saveDailyQuote(newQuote, currentDate)
        } else {
            val savedId = prefs.getInt("last_quote_id", -1)
            val quote = repository.getAllQuotes().find { it.id == savedId } ?: repository.getRandomQuote()
            _uiState.update { it.copy(currentQuote = quote) }
        }
    }

    private fun saveDailyQuote(quote: Quote, date: String) {
        prefs.edit().apply {
            putString("last_quote_date", date)
            putInt("last_quote_id", quote.id)
            apply()
        }
        _uiState.update { it.copy(currentQuote = quote) }
    }

    private fun loadFavorites() {
        val favoriteIds = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        val favorites = repository.getAllQuotes().filter { it.id.toString() in favoriteIds }.toSet()
        _uiState.update { it.copy(favoriteQuotes = favorites) }
    }

    private fun saveFavorites(favorites: Set<Quote>) {
        val favoriteIds = favorites.map { it.id.toString() }.toSet()
        prefs.edit().putStringSet("favorite_ids", favoriteIds).apply()
    }

    fun refreshQuote() {
        val newQuote = repository.getRandomQuote()
        _uiState.update { it.copy(currentQuote = newQuote) }
    }

    fun toggleFavorite(quote: Quote) {
        _uiState.update { state ->
            val newFavorites = if (state.favoriteQuotes.contains(quote)) {
                state.favoriteQuotes - quote
            } else {
                state.favoriteQuotes + quote
            }
            saveFavorites(newFavorites)
            state.copy(favoriteQuotes = newFavorites)
        }
    }
}

data class QuoteUiState(
    val currentQuote: Quote? = null,
    val favoriteQuotes: Set<Quote> = emptySet()
)
