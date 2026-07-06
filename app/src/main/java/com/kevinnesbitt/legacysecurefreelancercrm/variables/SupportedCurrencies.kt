package com.kevinnesbitt.legacysecurefreelancercrm.variables

enum class SupportedCurrency(val code: String, val symbol: String, val displayName: String) {
    USD("USD", "$", "US Dollar ($)"),
    EUR("EUR", "€", "Euro (€)"),
    GBP("GBP", "£", "British Pound (£)"),
    CAD("CAD", "$", "Canadian Dollar ($)"),
    AUD("AUD", "$", "Australian Dollar ($)"),
    INR("INR", "₹", "Indian Rupee (₹)"),
    PHP("PHP", "₱", "Philippine Peso (₱)"),
    BRL("BRL", "R$", "Brazilian Real (R$)"),
    JPY("JPY", "¥", "Japanese Yen (¥)"),
    CHF("CHF", "CHF", "Swiss Franc (CHF)")
}