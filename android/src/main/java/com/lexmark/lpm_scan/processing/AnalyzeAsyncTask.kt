package com.lexmark.lpm_scan.processing

import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import com.geniusscansdk.core.DocumentDetector
import com.geniusscansdk.core.LicenseException
import com.geniusscansdk.core.Quadrangle
import com.lexmark.lpm_scan.model.Page
import java.io.File


internal open class AnalyzeAsyncTask(private val context: Context) :
    AsyncTask<Page?, Void?, Quadrangle?>() {
    private var error: Exception? = null
    private val documentDetector: DocumentDetector

    init {
        documentDetector = DocumentDetector.create(context)
    }

    override fun doInBackground(vararg p0: Page?): Quadrangle? {
        return try {
            val scanContainer: Page? = p0[0]
            val file = scanContainer?.originalImage
            val imageFile: File = file!!
            documentDetector.detectDocument(imageFile)
        } catch (e: Exception) {
            error = e
            null
        }
    }

    override fun onPostExecute(result: Quadrangle?) {
        if (error is LicenseException) {
            AlertDialog.Builder(context)
                .setMessage((error as LicenseException).message)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }
}