package com.example.cashflow.dataClass

import java.util.Calendar

class TemplateTransaction(
    val id: Int,
    val name: String,
    val isIncome: Boolean,
    val amount: Double,
    val categoryId: Int,
    val accountId: Int
) {

//    fun createTransaction(date: Calendar, cityId: Int): Transactions {
//        // Si assume che l'ID della nuova transazione sarà generato altrove (es. dal database)
//        return Transactions(
//            isIncome = this.isIncome,
//            amount = this.amount,
//            date = date,
//            cityId = cityId,
//            categoryId = this.categoryId,
//            accountId = this.accountId
//        )
//    }

    override fun toString(): String {
        return "TemplateTransaction(id=$id, name='$name', isIncome=$isIncome, amount=$amount, categoryId=$categoryId, accountId=$accountId)"
    }
}
