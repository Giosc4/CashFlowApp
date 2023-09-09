package com.example.cashflow;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class TextRecognition {

    private Uri selectedImageUri;
    private Context context;
    private String recognizedText;

    public TextRecognition(Context context, Uri selectedImageUri) {
        this.context = context;
        this.selectedImageUri = selectedImageUri;
        setUpImg();
    }

    private void setUpImg() {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(context, selectedImageUri);

            FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            recognizer.processImage(image)
                    .addOnCompleteListener(new OnCompleteListener<FirebaseVisionText>() {
                        @Override
                        public void onComplete(@NonNull Task<FirebaseVisionText> task) {
                            if (task.isSuccessful()) {
                                FirebaseVisionText firebaseVisionText = task.getResult();
                                recognizedText = firebaseVisionText.getText();
                                Log.d("TextRecognition", "Recognized Text: " + recognizedText);

                                // Assuming you have a callback or some way to notify your UI with the recognized text.
                                // You can use recognizedText here or send it back to the calling class.
                            } else {
                                Log.e("TextRecognition", "Error recognizing text: " + task.getException());
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRecognizedText() {
        return recognizedText;
    }
}
