package com.example.cashflow;


import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class OCRManager {
    private Context context;

    public OCRManager(Context context){
        this.context = context;
    }

    public void processImage(Uri imageUri, final OCRListener listener) {
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(context, imageUri);

            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getCloudTextRecognizer();

            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            listener.onTextRecognized(result.getText());
                            System.out.println(result.getText());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onFailure(e);
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(e);
        }
    }

    public interface OCRListener {
        void onTextRecognized(String text);
        void onFailure(Exception e);
    }
}
