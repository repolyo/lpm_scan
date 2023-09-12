package com.lexmark.lpm_scan.model

import com.lexmark.lpm_scan.BuildConfig
import io.flutter.plugin.common.MethodCall

class ScanSettings private constructor() {
    companion object {
        val instance:ScanSettings by lazy {
            ScanSettings()
        }
    }

    var pdfFilename: String? = null
    var fileProvider: String? = null
    var detectionStatus: String? = null
    var ocr: Boolean = false

    fun load(poCall: MethodCall) {
        detectionStatus = poCall.argument("detection_status")
        pdfFilename = poCall.argument("pdfFilename") ?: "test.pdf"
        fileProvider = poCall.argument("fileprovider") ?: BuildConfig.LIBRARY_PACKAGE_NAME
        ocr = (poCall.argument("ocr") ?: "false").toBoolean()
    }
}