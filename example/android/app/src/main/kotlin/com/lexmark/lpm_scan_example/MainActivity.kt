package com.lexmark.lpm_scan_example

import com.lexmark.lpm_scan.model.DocumentManager
import io.flutter.embedding.android.FlutterActivity


class MainActivity: FlutterActivity() {

    override fun onResume() {
        super.onResume()
        val pageCount = DocumentManager.getInstance(this).pages.size
        print("pages in the document: $pageCount")
    }
}
