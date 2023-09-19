package com.lexmark.lpm_scan_example

import com.geniusscansdk.camera.ScanFragment
import com.lexmark.lpm_scan.model.DocumentManager
import io.flutter.embedding.android.FlutterFragmentActivity


class MainActivity: FlutterFragmentActivity(), ScanFragment.CameraCallbackProvider {

    override fun onResume() {
        super.onResume()


        val pageCount = DocumentManager.getInstance(this).pages.size
        print("pages in the document: $pageCount")
    }

    override fun getCameraCallback(): ScanFragment.Callback {
        return object : ScanFragment.Callback {
            override fun onCameraReady() {}
            override fun onCameraFailure() {}
            override fun onShutterTriggered() {}
            override fun onPreviewFrame(bytes: ByteArray, width: Int, height: Int, format: Int) {}
        }
    }
}
