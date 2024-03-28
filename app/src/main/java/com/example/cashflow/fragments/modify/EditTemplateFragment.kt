package com.example.cashflow.fragments.modify

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.db.*

class EditTemplateFragment () :
    Fragment() {
    private val viewModel: DataViewModel by viewModels()
    private val readSQL = viewModel.getReadSQL()
    private val writeSQL = viewModel.getWriteSQL()


}