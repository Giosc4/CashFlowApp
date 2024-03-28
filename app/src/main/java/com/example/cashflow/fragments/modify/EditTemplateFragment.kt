package com.example.cashflow.fragments.modify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashflow.R
import com.example.cashflow.db.*

class EditTemplateFragment() :
    Fragment() {
    private val viewModel: DataViewModel by viewModels()
    private var readSQL: ReadSQL? = null
    private var writeSQL: WriteSQL? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_budget, container, false)
        readSQL = viewModel.getReadSQL()
        writeSQL = viewModel.getWriteSQL()
        Log.d("EditTemplateFragment", "onCreateView")
        return view
    }


}