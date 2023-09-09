package com.lexmark.lpm_scan.enhance


import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.core.LicenseException
import com.geniusscansdk.core.RotationAngle
import com.lexmark.lpm_scan.model.Page
import com.lexmark.lpm_scan.processing.PageProcessor


internal open class RotateAsyncTask(private val context: Context, private val angle: RotationAngle) :
    AsyncTask<Page?, Void?, Void?>() {
    private var error: Exception? = null
    override fun doInBackground(vararg params: Page?): Void? {
        val scanContainer: Page? = params[0]
        try {
            if (scanContainer != null) {
                if (scanContainer.getQuadrangle() != null) {
                    scanContainer.setQuadrangle(scanContainer.getQuadrangle().rotate(angle))
                }
            }
            val imagePath = scanContainer?.originalImage?.absolutePath ?: ""
            GeniusScanSDK.rotateImage(imagePath, imagePath, angle)


            // original image was rotated, let's reprocess it
            if (scanContainer != null) {
                PageProcessor().processPage(context, scanContainer)
            }
        } catch (e: Exception) {
            error = e
        }
        return null
    }

    protected override fun onPostExecute(result: Void?) {
        if (error is LicenseException) {
            AlertDialog.Builder(context)
                .setMessage((error as LicenseException).message)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }
}