package com.lexmark.lpm_scan.enhance

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import com.geniusscansdk.core.TextLayout
import com.geniusscansdk.ocr.OCREngineProgressListener
import com.geniusscansdk.ocr.OcrConfiguration
import com.geniusscansdk.ocr.OcrProcessor
import com.geniusscansdk.ocr.OcrResult
import com.geniusscansdk.pdf.DocumentGenerator
import com.geniusscansdk.pdf.PDFDocument
import com.geniusscansdk.pdf.PDFImageProcessor
import com.geniusscansdk.pdf.PDFPage
import com.geniusscansdk.pdf.PDFSize
import com.lexmark.lpm_scan.R
import com.lexmark.lpm_scan.model.Page
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Date


open class PdfGenerationTask(
    var context: Context?,
    var pages: List<Page?>?,
    private var outputFile: File?,
    private var isOCREnabled: Boolean,
    private var listener: (Boolean, Exception?) -> Unit
): AsyncTask<Void, Integer, Exception>() {
    private val A4_SIZE = PDFSize(8.27, 11.69) // Size of A4 in inches
    private lateinit var progressDialog: ProgressDialog
    private var pageProgress: Int = 0


    override fun onPreExecute() {
        if (isOCREnabled) {
            progressDialog = ProgressDialog(context)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.setMax(100)
            progressDialog.setMessage("OCR in progress")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }
    }

    override fun doInBackground(vararg p0: Void?): Exception? {
        var ocrProcessor: OcrProcessor? = null

        if (isOCREnabled) {
            try {
                copyTessdataFiles()
            } catch (e: IOException) {
                return IOException("Cannot copy tessdata", e)
            }
            val ocrConfiguration = OcrConfiguration(mutableListOf("eng"), getTessdataDirectory())
            ocrProcessor = OcrProcessor(context, ocrConfiguration, object : OCREngineProgressListener() {
                override fun updateProgress(progress: Int) {
                    publishProgress(Integer(pageProgress + progress / (pages?.size ?: 0)))
                }
            })
        }

        val pdfPages = ArrayList<PDFPage>()
        var pageIndex = 0
        val pageList = pages!!

        for (page in pageList) {
            pageProgress = pageIndex * 100 / pageList.size
            val image = page?.enhancedImage
            var textLayout: TextLayout? = null
            if (isOCREnabled) {
                textLayout = try {
                    val result: OcrResult = ocrProcessor!!.processImage(image)
                    result.textLayout
                } catch (e: java.lang.Exception) {
                    return java.lang.Exception("OCR processing failed", e)
                }
            }

            // Export all pages in A4
            if (image != null) {
                pdfPages.add(PDFPage(image.absolutePath, A4_SIZE, textLayout))
            }
            pageIndex++
        }

        return try {
            // Here we don't protect the PDF document with a password
            val pdfDocument = PDFDocument(outputFile?.name ?: "test", null, null, Date(), Date(), pdfPages)
            val configuration = DocumentGenerator.Configuration(outputFile)
            DocumentGenerator(context).generatePDFDocument(pdfDocument, configuration)
            null
        } catch (e: java.lang.Exception) {
            e
        }
    }

    override fun onPostExecute(error: java.lang.Exception?) {
        super.onPostExecute(error)
        listener!!.invoke(error == null, error)
        if (isOCREnabled) {
            progressDialog.dismiss()
        }
    }

    protected fun onProgressUpdate(vararg values: Int?) {
        if (isOCREnabled) {
            progressDialog.progress = values[0]!!
        }
    }

    @Throws(IOException::class)
    private fun copyTessdataFiles() {
        val tessdataDir = getTessdataDirectory()
        if (tessdataDir.exists()) {
            return
        }
        tessdataDir.mkdir()
        val istm = context!!.resources.openRawResource(R.raw.eng)
        val engFile = File(tessdataDir, "eng.traineddata")
        val out: OutputStream = FileOutputStream(engFile)
        val buffer = ByteArray(1024)
        var len = istm.read(buffer)
        while (len != -1) {
            out.write(buffer, 0, len)
            len = istm.read(buffer)
        }
    }

    private fun getTessdataDirectory(): File {
        return File(context!!.getExternalFilesDir(null), "tessdata")
    }

    private class PDFNoopImageProcessor : PDFImageProcessor() {
        override fun process(inputFilePath: String): String {
            return inputFilePath
        }
    }
}