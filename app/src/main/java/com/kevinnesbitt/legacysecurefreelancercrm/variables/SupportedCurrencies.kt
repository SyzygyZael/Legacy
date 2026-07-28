package com.kevinnesbitt.legacysecurefreelancercrm.variables

enum class SupportedCurrency(val code: String, val symbol: String, val displayName: String, val USDConversion: Float) {
    USD("USD", "$", "US Dollar ($)", 1f),
    EUR("EUR", "€", "Euro (€)", 0.88f),
    GBP("GBP", "£", "British Pound (£)", 0.75f),
    CAD("CAD", "$", "Canadian Dollar ($)", 1.41f),
    AUD("AUD", "$", "Australian Dollar ($)", 1.43f),
    INR("INR", "₹", "Indian Rupee (₹)", 96.57f),
    PHP("PHP", "₱", "Philippine Peso (₱)", 61.69f),
    BRL("BRL", "R$", "Brazilian Real (R$)", 5.08f),
    JPY("JPY", "¥", "Japanese Yen (¥)", 163.26f),
    CHF("CHF", "CHF ", "Swiss Franc (CHF)", 0.82f),
    IDR("IDR", "Rp ", "Indonesian Rupiah (Rp)", 18070.00f),
    QAR("QAR", "QAR ", "Qatari Riyal", 3.64f)
}