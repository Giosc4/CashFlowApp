package com.example.cashflow.box;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.R;

public class box_template_fragment extends Fragment {

    public box_template_fragment() {
    }

    private void addButtonsBox(int numberOfButtons, View view) {
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(2);

        for (int i = 0; i < numberOfButtons; i++) {
            Button button = new Button(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 300;
            params.height = 100;
            params.rightMargin = 20;
            params.leftMargin = 20;
            params.topMargin = 20;
            params.setGravity(Gravity.CENTER);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            button.setLayoutParams(params);
            button.setText("Button " + (i + 1));
            button.setTextColor(Color.WHITE);
            button.setBackgroundColor(Color.parseColor("#37a63e"));
            button.setId(View.generateViewId());

            final int buttonId = i + 1; // Identificativo univoco per il pulsante, basato sull'indice i
            button.setTag(buttonId); // Imposta il tag del pulsante con il suo identificativo

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Recupera l'identificativo dal tag del pulsante
                    int id = (int) v.getTag();
                    Log.d("Button", "Button " + id + " clicked");
                }
            });
            gridLayout.addView(button);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout per questo fragment
        return inflater.inflate(R.layout.box_fragment_template, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addButtonsBox(5, view);
    }

}