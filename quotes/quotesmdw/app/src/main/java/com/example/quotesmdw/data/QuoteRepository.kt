package com.example.quotesmdw.data

import kotlin.random.Random

class QuoteRepository {
    private val quotes = listOf(
        Quote(1, "The only way to do great work is to love what you do.", "Steve Jobs"),
        Quote(2, "Believe you can and you're halfway there.", "Theodore Roosevelt"),
        Quote(3, "The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"),
        Quote(4, "It does not matter how slowly you go as long as you do not stop.", "Confucius"),
        Quote(5, "Everything you've ever wanted is on the other side of fear.", "George Addair"),
        Quote(6, "Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
        Quote(7, "Hardships often prepare ordinary people for an extraordinary destiny.", "C.S. Lewis"),
        Quote(8, "The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"),
        Quote(9, "What you get by achieving your goals is not as important as what you become by achieving your goals.", "Zig Ziglar"),
        Quote(10, "I can't change the direction of the wind, but I can adjust my sails to always reach my destination.", "Jimmy Dean")
    )

    fun getRandomQuote(): Quote {
        return quotes[Random.nextInt(quotes.size)]
    }

    fun getAllQuotes(): List<Quote> = quotes
}
