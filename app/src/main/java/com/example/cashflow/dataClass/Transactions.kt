package com.example.cashflow.dataClass

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Transactions {
    // Getter and Setter Methods
    var id = 0
    var isIncome // True for income, false for expense
            : Boolean
    var amountValue: Double
        get() = field
        set
    var date: Calendar
    var cityId: Int
    var categoryId: Int
    var accountId: Int

    constructor(
        income: Boolean,
        amount: Double,
        date: Calendar,
        cityId: Int,
        categoryId: Int,
        accountId: Int
    ) {
        isIncome = income
        amountValue = amount

        val calendar = Calendar.getInstance()
        this.date = calendar
        this.cityId = cityId
        this.categoryId = categoryId
        this.accountId = accountId
    }

    constructor(
        id: Int,
        income: Boolean,
        amount: Double,
        date: Calendar,
        cityId: Int,
        categoryId: Int,
        accountId: Int
    ) {
        this.id = id
        isIncome = income
        amountValue = amount
        this.date = date
        this.cityId = cityId
        this.categoryId = categoryId
        this.accountId = accountId
    }

    override fun toString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return "Transactions{" +
                "id=" + id +
                ", income=" + isIncome +
                ", amount=" + amountValue +
                ", date=" + dateFormat.format(date.time) +
                ", cityId=" + cityId +
                ", categoryId=" + categoryId +
                ", accountId=" + accountId +
                '}'
    }

    fun printOnApp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return "Transactions" +
                "id= " + id +
                ", IsIncome= " + (if (isIncome) "Income" else "Expense") +
                ", amount= " + amountValue +
                ", date= " + dateFormat.format(date.time) +
                ", cityId= " + cityId +
                ", categoryId= " + categoryId +
                ", accountId= " + accountId
    }
}
