package com.example.cashflow.dataClass

class Account {
    var id = 0
        private set
    var name: String? = null
    var balance = 0.0

    constructor()
    constructor(name: String?, balance: Double) {
        this.name = name
        this.balance = balance
    }

    constructor(id: Int, name: String?, balance: Double) {
        this.id = id
        this.name = name
        this.balance = balance
    }

    fun isNotEmpty(): Boolean {
        return name != null
    }



}
