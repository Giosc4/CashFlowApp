package com.example.cashflow.dataClass

import java.util.Calendar

class TemplateTransaction(
    val id: Int,
    val name: String,
    val income: Boolean,
    val amount: Double,
    val category_id: Int,
    val account_id: Int
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
        return "TemplateTransaction(id=$id, name='$name', isIncome=$income, amount=$amount, categoryId=$category_id, accountId=$account_id)"
    }
}
