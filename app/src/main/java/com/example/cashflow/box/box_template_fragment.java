package com.example.cashflow.box;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashflow.R;

public class box_template_fragment extends Fragment {
    TextView boxTitle;
    Button boxButton;
    TextView boxTextView;

    public box_template_fragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_fragment_template, container, false);

        LinearLayout dynamicContainer = view.findViewById(R.id.boxContainer);

        for (int i = 0; i < 3; i++) {
            // Inflaziona il layout per ogni elemento dinamico
            View dynamicView = inflater.inflate(R.layout.box_fragment_template, dynamicContainer, false);

            // Trova le viste all'interno di dynamicView
            boxTitle = new TextView(getContext());
            boxButton = new Button(getContext());
            boxTextView = new TextView(getContext());

            // Imposta i testi per le viste
            boxTitle.setText("Titolo " + (i + 1));
            boxButton.setText("Pulsante " + (i + 1));
            boxTextView.setText("Testo aggiuntivo " + (i + 1));

            // Aggiungi dynamicView al container
            dynamicContainer.addView(dynamicView);
        }

        return view;
    }

}