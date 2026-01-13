package com.example.core.settings

object CurrencyConverter {
    private const val USD_RATE = 1.16
    fun convertPrice(amountInEuro: Double, currencyIndex: Int): String {
        return if (currencyIndex == 0) {
            val usdRate = 1.16
            "$${"%.2f".format(amountInEuro * usdRate)}"
        } else {
            "€${"%.2f".format(amountInEuro)}"
        }
    }

    fun toEuro(amount: Double, currencyIndex: Int): Double {
        return when (currencyIndex) {
            0 -> amount / USD_RATE
            else -> amount
        }
    }

    fun fromEuro(amountInEuro: Double, currencyIndex: Int): Double {
        return when (currencyIndex) {
            0 -> amountInEuro * USD_RATE
            else -> amountInEuro
        }
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)
}