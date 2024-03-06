package com.example.cashflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCRManager {
    private TessBaseAPI tessBaseAPI;
    private Context context;


    public OCRManager(Context context) {
        this.context = context;
        tessBaseAPI = new TessBaseAPI();
        // Copia i file .traineddata necessari// Ensure the .traineddata file for the required language is present
        ensureTrainedDataFile("ita");

        // Costruisci il percorso di destinazione nella memoria interna
        String dataPath = context.getFilesDir() + "/tesseract/";
        // Assicurati che il percorso termini con '/' prima di passarlo a TessBaseAPI.init
        if (!dataPath.endsWith("/")) {
            dataPath += "/";
        }
        // Inizializza Tesseract con il percorso e la lingua corretti
        boolean initSuccess = tessBaseAPI.init(dataPath, "ita");
        Log.d("OCRManager", "Tesseract initialization success: " + initSuccess);

        if (!initSuccess) {
            // Gestisci il caso di fallimento dell'inizializzazione di Tesseract
            throw new IllegalStateException("Could not initialize Tesseract with data path: " + dataPath);
        }
    }


    public interface OCRListener {
        void onTextRecognized(String value);

        void onTextNotRecognized(String error);

        void onFailure(Exception e);
    }

    public void releaseResources() {
        if (tessBaseAPI != null) {
            tessBaseAPI.end();
        }
    }

    private void ensureTrainedDataFile(String language) {
        InputStream in = null;
        OutputStream out = null;
        try {
            // Costruisci il percorso nella cartella assets
            String assetPath = "tessdata/" + language + ".traineddata";

            // Costruisci il percorso di destinazione nella memoria interna
            String destPath = context.getFilesDir() + "/tesseract/tessdata/" + language + ".traineddata";

            // Crea le cartelle se non esistono
            File file = new File(context.getFilesDir() + "/tesseract/tessdata/");
            if (!file.exists()) {
                file.mkdirs();
            }

            // Controlla se il file esiste già per evitare di copiarlo più volte
            File destFile = new File(destPath);
            if (!destFile.exists()) {
                // Apre gli stream
                in = context.getAssets().open(assetPath);
                out = new FileOutputStream(destPath);

                // Copia i byte dal file degli assets alla memoria interna
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
            } else {
                Log.i("OCRManager", language + ".traineddata already exists, no need to copy.");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Gestisci l'eccezione come preferisci
        } finally {
            // Chiudi gli stream per evitare perdite di memoria
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void processImage(Uri imageUri, OCRListener listener) {

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
            if (bitmap == null) {
                listener.onFailure(new Exception("Failed to decode bitmap from Uri."));
                return;
            }
            tessBaseAPI.setImage(bitmap);

            String extractedText = tessBaseAPI.getUTF8Text();

            if (extractedText != null && !extractedText.isEmpty()) {
                listener.onTextRecognized(extractedText);
                Log.d("OCRManager", "Text recognized: " + extractedText);
            } else {
                listener.onTextNotRecognized("No text recognized");
                Log.d("OCRManager", "No text recognized");
            }
        } catch (Exception e) {
            listener.onFailure(e);
            Log.e("OCRManager", "Error processing image", e);
        }
    }
}
