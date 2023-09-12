package com.lexmark.lpm_scan.camera

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.geniusscansdk.camera.FileImageCaptureCallback
import com.geniusscansdk.camera.FocusIndicator
import com.geniusscansdk.camera.ScanFragment
import com.geniusscansdk.camera.ScanFragment.CameraCallbackProvider
import com.geniusscansdk.camera.realtime.BorderDetector.BorderDetectorListener
import com.geniusscansdk.core.GeniusScanSDK
import com.geniusscansdk.core.LicenseException
import com.geniusscansdk.core.QuadStreamAnalyzer
import com.geniusscansdk.core.RotationAngle
import com.lexmark.lpm_scan.BuildConfig
import com.lexmark.lpm_scan.R
import com.lexmark.lpm_scan.enhance.ImageProcessingActivity
import com.lexmark.lpm_scan.enhance.PdfGenerationTask
import com.lexmark.lpm_scan.model.DocumentManager
import com.lexmark.lpm_scan.model.Page
import java.io.File
import java.util.UUID


class ScanActivity : AppCompatActivity(), CameraCallbackProvider {
    private lateinit var scanFragment: ScanFragment
    private var userGuidanceTextView: TextView? = null
    private var cameraPermissionGranted = false

    private val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            android.R.string.no, Toast.LENGTH_SHORT).show()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Go full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_scan)
        val captureButton = findViewById<Button>(R.id.captureButton)
        captureButton.setOnClickListener { view: View? -> takePicture() }

        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener { view: View? -> onCancel() }

        val doneButton = findViewById<Button>(R.id.doneButton)
        doneButton.setOnClickListener { view: View? -> onComplete() }

        userGuidanceTextView = findViewById(R.id.user_guidance)
        val focusIndicator: FocusIndicator = findViewById(R.id.focus_indicator)
        scanFragment = ScanFragment.createBestForDevice()
        supportFragmentManager.beginTransaction().replace(R.id.scan_fragment_layout, scanFragment).commit()
        scanFragment.setPreviewAspectFill(false)
        scanFragment.setRealTimeDetectionEnabled(true)
        scanFragment.setFocusIndicator(focusIndicator)
        scanFragment.setAutoTriggerAnimationEnabled(true)
        scanFragment.setBorderDetectorListener(object : BorderDetectorListener {
            override fun onBorderDetectionResult(result: QuadStreamAnalyzer.Result) {
                if (result.status == QuadStreamAnalyzer.Status.TRIGGER) {
                    takePicture()
                }
                updateUserGuidance(result)
            }

            override fun onBorderDetectionFailure(e: Exception) {
                scanFragment.setPreviewEnabled(false)
                AlertDialog.Builder(this@ScanActivity)
                    .setMessage(e.message)
                    .setCancelable(false)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { dialogInterface: DialogInterface?, i: Int -> finish() }
                    .show()
            }
        })
        cameraPermissionGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        }

//        doneButton.isEnabled = DocumentManager.getInstance(this).pages.isNotEmpty()
    }

    override fun onResume() {
        super.onResume()
        if (cameraPermissionGranted) {
            scanFragment!!.initializeCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            cameraPermissionGranted = grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            // Camera will be initialized in onResume
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun takePicture() {
        val outputFile = File(getExternalFilesDir(null), UUID.randomUUID().toString() + ".jpeg")
        scanFragment!!.takePicture(object : FileImageCaptureCallback(outputFile) {
            override fun onImageCaptured(imageOrientation: RotationAngle) {
                val page = Page(outputFile)
                RotateTask(page, imageOrientation).execute()
            }

            override fun onError(e: Exception) {
                Toast.makeText(this@ScanActivity, "Capture failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Capture failed", e)
            }
        })
    }

    private fun onCancel() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Cancel")
        builder.setMessage("Aborting scan operation?")
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
            finish()
        }

        builder.show()
    }

    private fun onComplete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Leaving Lexmark App")
        builder.setMessage("Your scan has saved. Redirecting to your files app.")
        builder.setNegativeButton("Stay", negativeButtonClick)
        builder.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
            shareDocument()
        }
        builder.show()
    }

    private fun shareDocument() {
        var pdfFilename: String? = intent.getStringExtra("pdfFilename")
        var fileprovider: String? = intent.getStringExtra("fileprovider")
        val pages = DocumentManager.getInstance(this).pages
        val outputFile = File(externalCacheDir, pdfFilename)
        val ocr = intent.getBooleanExtra("ocr", false)
        PdfGenerationTask(this, pages, outputFile, ocr) label@{ isSuccess, error ->
            if (!isSuccess) {
                Log.e(TAG, "Error generating PDF", error)
                Toast.makeText(this@ScanActivity, error?.message, Toast.LENGTH_LONG)
                    .show()
                return@label
            }

            if (null == fileprovider) {
                fileprovider = BuildConfig.LIBRARY_PACKAGE_NAME
            }

            // View generated PDF document with another compatible installed app
            val uri = FileProvider.getUriForFile(
                this@ScanActivity,
                "$fileprovider.fileprovider",
                outputFile
            )
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intent)
        }.execute()
    }

    private fun updateUserGuidance(result: QuadStreamAnalyzer.Result) {
        val textResId = getUserGuidanceResId(result)
        if (textResId == 0) {
            userGuidanceTextView!!.visibility = View.INVISIBLE
        } else {
            userGuidanceTextView!!.visibility = View.VISIBLE
            val detectionStatus = intent.getStringExtra("detection_status")
            if (textResId == R.string.detection_status_custom && null != detectionStatus) {
                userGuidanceTextView!!.text = detectionStatus
            }
            else {
                userGuidanceTextView!!.setText(textResId)
            }
        }
    }

    private fun getUserGuidanceResId(result: QuadStreamAnalyzer.Result): Int {
        if (result.status == QuadStreamAnalyzer.Status.NOT_FOUND || result.resultQuadrangle == null) {
            return R.string.detection_status_custom
        } else if (result.status == QuadStreamAnalyzer.Status.SEARCHING || result.status == QuadStreamAnalyzer.Status.ABOUT_TO_TRIGGER) {
            return R.string.detection_status_found
        }
        return 0
    }

    override fun getCameraCallback(): ScanFragment.Callback {
        return object : ScanFragment.Callback {
            override fun onCameraReady() {}
            override fun onCameraFailure() {}
            override fun onShutterTriggered() {}
            override fun onPreviewFrame(bytes: ByteArray, width: Int, height: Int, format: Int) {}
        }
    }

    internal inner class RotateTask(private val page: Page, private val rotationAngle: RotationAngle) :
        AsyncTask<Void?, Void?, Void?>() {
        private lateinit var progressDialog: ProgressDialog
        private var exception: Exception? = null


        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(this@ScanActivity)
            progressDialog.show()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            val path = page.originalImage.absolutePath
            // Even if rotation angle is 0, we perform a rotation to apply exif orientation
            try {
                GeniusScanSDK.rotateImage(path, path, rotationAngle)
            } catch (e: Exception) {
                exception = e
            }
            return null
        }

        protected override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (exception is LicenseException) {
                AlertDialog.Builder(this@ScanActivity)
                    .setMessage((exception as LicenseException).message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            } else if (exception != null) {
                throw RuntimeException(exception)
            } else {
                val intent = Intent(this@ScanActivity, ImageProcessingActivity::class.java)
                intent.putExtra(ImageProcessingActivity.EXTRA_PAGE, page)
                startActivity(intent)

            }
        }
    }

    companion object {
        private val TAG = ScanActivity::class.java.simpleName
        private const val PERMISSION_REQUEST_CODE = 1
    }
}
