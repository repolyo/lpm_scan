package com.lexmark.lpm_scan.enhance

import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import com.geniusscansdk.core.LicenseException
import com.lexmark.lpm_scan.model.Page
import com.lexmark.lpm_scan.processing.PageProcessor


internal open class EnhanceAsyncTask(private val context: Context) :
    AsyncTask<Page?, Void?, Void?>() {
    private var error: Exception? = null
    override fun doInBackground(vararg p0: Page?): Void? {
        try {
            val page: Page? = p0[0]
            if (page != null) {
                PageProcessor().processPage(context, page)
            }
        } catch (e: Exception) {
            error = e
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        if (error is LicenseException) {
            AlertDialog.Builder(context)
                .setMessage((error as LicenseException).message)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
    }
}