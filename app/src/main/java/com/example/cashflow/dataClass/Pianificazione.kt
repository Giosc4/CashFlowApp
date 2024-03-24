package com.example.cashflow.dataClass

class Planning(
    val id: Int,
    val income: Int,
    val amount: Double,
    val date: String,
    val cityId: Int?,
    val categoryId: Int,
    val accountId: Int?,
    val repetition: String,
    val endDate: String
) {
    constructor(
        income: Int,
        amount: Double,
        date: String,
        cityId: Int?,
        categoryId: Int,
        accountId: Int?,
        repetition: String,
        endDate: String
    ) : this(0, income, amount, date, cityId, categoryId, accountId, repetition, endDate)
}
