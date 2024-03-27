package com.example.cashflow.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class OCRManager(private val context: Context) {
    private val tessBaseAPI: TessBaseAPI?

    init {
        tessBaseAPI = TessBaseAPI()
        // Copia i file .traineddata necessari// Ensure the .traineddata file for the required language is present
        ensureTrainedDataFile("ita")

        // Costruisci il percorso di destinazione nella memoria interna
        var dataPath = context.filesDir.toString() + "/tesseract/"
        // Assicurati che il percorso termini con '/' prima di passarlo a TessBaseAPI.init
        if (!dataPath.endsWith("/")) {
            dataPath += "/"
        }
        // Inizializza Tesseract con il percorso e la lingua corretti
        val initSuccess = tessBaseAPI.init(dataPath, "ita")
        Log.d("OCRManager", "Tesseract initialization success: $initSuccess")
        check(initSuccess) {
            // Gestisci il caso di fallimento dell'inizializzazione di Tesseract
            "Could not initialize Tesseract with data path: $dataPath"
        }
    }

    interface OCRListener {
        fun onTextRecognized(value: String?)
        fun onTextNotRecognized(error: String?)
        fun onFailure(e: Exception?)
    }

    fun releaseResources() {
        tessBaseAPI?.end()
    }

    private fun ensureTrainedDataFile(language: String) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            // Costruisci il percorso nella cartella assets
            val assetPath = "tessdata/$language.traineddata"

            // Costruisci il percorso di destinazione nella memoria interna
            val destPath =
                context.filesDir.toString() + "/tesseract/tessdata/" + language + ".traineddata"

            // Crea le cartelle se non esistono
            val file = File(context.filesDir.toString() + "/tesseract/tessdata/")
            if (!file.exists()) {
                file.mkdirs()
            }

            // Controlla se il file esiste già per evitare di copiarlo più volte
            val destFile = File(destPath)
            if (!destFile.exists()) {
                // Apre gli stream
                `in` = context.assets.open(assetPath)
                out = FileOutputStream(destPath)

                // Copia i byte dal file degli assets alla memoria interna
                val buffer = ByteArray(1024)
                var read: Int
                while (`in`.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.flush()
            } else {
                Log.i("OCRManager", "$language.traineddata already exists, no need to copy.")
            }
        } catch (e: IOException) {
            e.printStackTrace() // Gestisci l'eccezione come preferisci
        } finally {
            // Chiudi gli stream per evitare perdite di memoria
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun processImage(imageUri: Uri?, listener: OCRListener) {
        try {
            val bitmap = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(
                    imageUri!!
                )
            )
            if (bitmap == null) {
                listener.onFailure(Exception("Failed to decode bitmap from Uri."))
                return
            }
            tessBaseAPI!!.setImage(bitmap)
            val extractedText = tessBaseAPI.getUTF8Text()
            if (extractedText != null && !extractedText.isEmpty()) {
                listener.onTextRecognized(extractedText)
                Log.d("OCRManager", "Text recognized: $extractedText")
            } else {
                listener.onTextNotRecognized("No text recognized")
                Log.d("OCRManager", "No text recognized")
            }
        } catch (e: Exception) {
            listener.onFailure(e)
            Log.e("OCRManager", "Error processing image", e)
        }
    }
}
