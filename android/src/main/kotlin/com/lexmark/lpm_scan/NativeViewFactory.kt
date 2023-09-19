package com.lexmark.lpm_scan

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeViewFactory(private val fragmentMgr: ScanInit?) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return NativeView(context, viewId, fragmentMgr)
    }
}