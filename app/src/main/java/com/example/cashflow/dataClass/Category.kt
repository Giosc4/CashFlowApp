package com.example.cashflow.dataClass

class Category {
    // Getter and Setter methods
    var id = 0
    var name: String? = null
    var description: String? = null

    constructor()
    constructor(name: String?, description: String?) {
        this.name = name
        this.description = description
    }

    constructor(id: Int, name: String?, description: String?) {
        this.id = id
        this.name = name
        this.description = description
    }
}
