package com.example.cashflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

public class TextRecognition {

    private Uri selectedImageUri;
    private Context context;

    private String recognizedText;

    public TextRecognition(Context context, Uri selectedImageUri) {
        this.context = context;
        this.selectedImageUri = selectedImageUri;
        System.out.println("sei entrato nella classe text Recognition");
        System.out.println("Uri selectedImageUri " + selectedImageUri);

        // Carica l'immagine dall'URI e effettua il riconoscimento del testo
        initializeTextRecognition();
    }

    private void initializeTextRecognition() {
        if (selectedImageUri != null) {
            try {
                // Verifica che l'URI sia valido
                if (isValidUri(selectedImageUri)) {
                    // Carica l'immagine dall'URI
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);
                    System.out.println("imageBitmap " + imageBitmap);

                    // Effettua il riconoscimento del testo
                    recognizedText = recognizeText(imageBitmap);
                    System.out.println("recognizedText " + recognizedText);

                    // Rilascia la memoria del Bitmap
                    imageBitmap.recycle();
                } else {
                    // Gestisci il caso in cui l'URI non sia valido
                    System.out.println("Errore: URI dell'immagine non valido");
                }
            } catch (IOException e) {
                // Gestione delle eccezioni
                e.printStackTrace();
                System.out.println("Errore durante il caricamento dell'immagine o il riconoscimento del testo");
            }
        }
    }

    private boolean isValidUri(Uri uri) {
        return uri != null && uri.getScheme() != null && uri.getScheme().startsWith("content");
    }


    // funzione recognizeText per il riconoscimento del testo
    private String recognizeText(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        System.out.println("textRecognizer " + textRecognizer.toString());
        if (!textRecognizer.isOperational()) {
            return "Errore: Il riconoscimento del testo non è disponibile";
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);

        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.valueAt(i);
            List<? extends Text> textComponents = textBlock.getComponents();

            for (Text currentText : textComponents) {
                resultBuilder.append(currentText.getValue());
                resultBuilder.append(" ");
            }
        }

        System.out.println("resultBuilder.toString().trim() " + resultBuilder.toString().trim());
        return resultBuilder.toString().trim();
    }

    public String getRecognizedText() {
        return recognizedText;
    }
}
