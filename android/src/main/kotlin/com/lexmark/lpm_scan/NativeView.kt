package com.lexmark.lpm_scan

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import io.flutter.plugin.platform.PlatformView

internal class NativeView(context: Context, id: Int, fragmentMgr: ScanInit?) :
    PlatformView {

    private var view : ScannerFragment? = ScannerFragment(context, fragmentMgr)

    override fun getView(): View {
        return view!!
    }

    override fun dispose() {
        view = null
    }

}