package com.example.cashflow.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.cashflow.R;

public class NewCategoryFragment extends Fragment {

    private EditText edtNameCategory, editTextNome, editTextImporto;
    private Button btnCreateBudget, btnCreateCategory;

    public NewCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_category, container, false);

        // Initialize views
        edtNameCategory = view.findViewById(R.id.edtNameCategory);
        editTextNome = view.findViewById(R.id.editTextNome);
        editTextImporto = view.findViewById(R.id.editTextImporto);
        btnCreateBudget = view.findViewById(R.id.btnCreateBufget);
        btnCreateCategory = view.findViewById(R.id.btnCreateCategory);

        // Initially hide budget fields
        editTextNome.setVisibility(View.GONE);
        editTextImporto.setVisibility(View.GONE);

        // Set click listener for the "Create Budget" button
        btnCreateBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show budget fields when "Create Budget" is clicked
                editTextNome.setVisibility(View.VISIBLE);
                editTextImporto.setVisibility(View.VISIBLE);
                Log.d("NewCategoryFragment", "Create Budget button clicked");
            }
        });

        // Set click listener for the "Create Category" button
        btnCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategoryAndBudget();
            }
        });

        return view;
    }

    private void saveCategoryAndBudget() {
        // Extract category and budget information from EditTexts
        String categoryName = edtNameCategory.getText().toString();
        String budgetName = editTextNome.getText().toString();
        String budgetAmount = editTextImporto.getText().toString();

        // Implement your saving logic here
        // Check if the budget name and amount are not empty
        if (!budgetName.isEmpty() && !budgetAmount.isEmpty()) {
            Log.d("NewCategoryFragment 1", "Category: " + categoryName + ", Budget: " + budgetName + ", Amount: " + budgetAmount);
        } else {
            Log.d("NewCategoryFragment 2", "Category: " + categoryName);
        }
        Log.d("NewCategoryFragment 3", "Category: " + categoryName + ", Budget: " + budgetName + ", Amount: " + budgetAmount);
        // After saving, you might want to clear the fields or show a confirmation message
    }
}
