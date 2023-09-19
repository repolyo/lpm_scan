package com.lexmark.lpm_scan

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.geniusscansdk.camera.FileImageCaptureCallback
import com.geniusscansdk.camera.FocusIndicator
import com.geniusscansdk.camera.ScanFragment
import com.geniusscansdk.camera.realtime.BorderDetector
import com.geniusscansdk.core.QuadStreamAnalyzer
import com.geniusscansdk.core.RotationAngle
import com.lexmark.lpm_scan.camera.ScanActivity
import com.lexmark.lpm_scan.model.Page
import com.lexmark.lpm_scan.model.ScanSettings
import io.flutter.embedding.android.FlutterFragmentActivity
import java.io.File
import java.util.UUID


class ScannerFragment(context: Context, fragmentMgr: ScanInit?) : FrameLayout(context,null), ScanFragment.CameraCallbackProvider {
    private var scanFragment: ScanFragment
    private var userGuidanceTextView: TextView? = null
    private var cameraPermissionGranted = false

    private val negativeButtonClick = { dialog: DialogInterface, which: Int ->
//        Toast.makeText(requireContext(),
//            android.R.string.no, Toast.LENGTH_SHORT).show()
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_scan,this)
        val captureButton = view.findViewById<Button>(R.id.captureButton)
        captureButton.setOnClickListener { view: View? -> takePicture() }

        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener { view: View? -> onCancel() }

        val doneButton = view.findViewById<Button>(R.id.doneButton)
        doneButton.setOnClickListener { view: View? -> onComplete() }

        userGuidanceTextView = view.findViewById(R.id.user_guidance)
        val focusIndicator: FocusIndicator = view.findViewById(R.id.focus_indicator)
        scanFragment = ScanFragment.createBestForDevice()

        if (context is FragmentActivity) {
            context.supportFragmentManager.beginTransaction()?.replace(R.id.scan_fragment_layout, scanFragment)?.commit()
        }

//        fragmentMgr?.beginTransaction()?.replace(R.id.scan_fragment_layout, scanFragment)?.commit()
        scanFragment.setPreviewAspectFill(false)
        scanFragment.setRealTimeDetectionEnabled(true)
        scanFragment.setFocusIndicator(focusIndicator)
        scanFragment.setAutoTriggerAnimationEnabled(true)
        scanFragment.setBorderDetectorListener(object : BorderDetector.BorderDetectorListener {
            override fun onBorderDetectionResult(result: QuadStreamAnalyzer.Result) {
                if (result.status == QuadStreamAnalyzer.Status.TRIGGER) {
                    takePicture()
                }
                updateUserGuidance(result)
            }

            override fun onBorderDetectionFailure(e: Exception) {
                scanFragment.setPreviewEnabled(false)

            }
        })

        cameraPermissionGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!cameraPermissionGranted) {
//            ActivityCompat.requestPermissions(context.getac, arrayOf(Manifest.permission.CAMERA),
//                PERMISSION_REQUEST_CODE
//            )
        } else {
            //scanFragment!!.initializeCamera()
        }

        fragmentMgr?.onCreatedScanner(this)
    }

    fun initializeCamera(fragmentMgr: FragmentManager?) {
        if (null != fragmentMgr) {
            fragmentMgr.beginTransaction()?.replace(R.id.scan_fragment_layout, scanFragment)?.commit()
            //scanFragment!!.initializeCamera()
        }
    }

    private fun updateUserGuidance(result: QuadStreamAnalyzer.Result) {
        val textResId = getUserGuidanceResId(result)
        if (textResId == 0) {
            userGuidanceTextView!!.visibility = View.INVISIBLE
        } else {
            val settings = ScanSettings.instance
            userGuidanceTextView!!.visibility = View.VISIBLE
            if (textResId == R.string.detection_status_custom && null != settings.detectionStatus) {
                userGuidanceTextView!!.text = settings.detectionStatus
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


    private fun takePicture() {
        val outputFile = File(UUID.randomUUID().toString() + ".jpeg")
        scanFragment!!.takePicture(object : FileImageCaptureCallback(outputFile) {
            override fun onImageCaptured(imageOrientation: RotationAngle) {
                val page = Page(outputFile)
                //RotateTask(page, imageOrientation).execute()
            }

            override fun onError(e: Exception) {
                Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Capture failed", e)
            }
        })
    }

    private fun onCancel() {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Cancel")
        builder.setMessage("Aborting scan operation?")
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
//            DocumentManager.getInstance(this).clear(this)
//            finish()
        }

        builder.show()
    }

    private fun onComplete() {
        scanFragment!!.initializeCamera()

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Leaving Lexmark App")
        builder.setMessage("Your scan has saved. Redirecting to your files app.")
        builder.setNegativeButton("Stay", negativeButtonClick)
        builder.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
//            shareDocument()
        }
        builder.show()
    }


    companion object {
        private val TAG = ScanActivity::class.java.simpleName
        private const val PERMISSION_REQUEST_CODE = 1
    }
}