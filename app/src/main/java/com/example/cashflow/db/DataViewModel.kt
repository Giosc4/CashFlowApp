package com.example.cashflow.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.cashflow.db.*

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val sqliteDB = SQLiteDB.getInstance(application)
    private val readSQL = ReadSQL(sqliteDB.writableDatabase)
    private val writeSQL = WriteSQL(sqliteDB.writableDatabase)

    fun getReadSQL(): ReadSQL = readSQL
    fun getWriteSQL(): WriteSQL = writeSQL
}
