package com.lexmark.lpm_scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.geniusscansdk.scanflow.ScanConfiguration
import com.lexmark.lpm_scan.camera.ScanActivity
import com.lexmark.lpm_scan.enhance.PdfGenerationTask
import com.lexmark.lpm_scan.model.DocumentManager
import com.lexmark.lpm_scan.model.ScanSettings
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.platform.PlatformViewRegistry
import java.io.File


/** LpmScanPlugin */
class LpmScanPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener,
  PluginRegistry.RequestPermissionsResultListener, ScanInit {
  val REQUEST_CODE: Int = 100

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var activity: Activity? = null
  private lateinit var _registry: PlatformViewRegistry
  private lateinit var context: Context
  private lateinit var _result: Result;
  private var scanner: ScannerFragment? = null
  private var fragmentMgr: FragmentManager? = null

  // Permission request management
  private var requestingPermission = false
  private var permissionRequestResultCallback: Result? = null

  private fun initWithActivity(act: Activity) {
    activity = act
//    ScannerFragment.scanner?.initializeCamera()
    fragmentMgr = (activity as FragmentActivity).supportFragmentManager
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    _registry = flutterPluginBinding.platformViewRegistry

    _registry.registerViewFactory("video_player_view", NativeViewFactory(this))
    context = flutterPluginBinding.applicationContext

    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "lpm_scan")
    channel.setMethodCallHandler(this)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    permissionRequestResultCallback = result;

    _result = result
    if (call.method == "scanWithConfiguration") {
      scanWithConfig(call, result)
    }
    else if (call.method == "generateDocument") {
      generateDocument(call, result);
    }
    else if (call.method == "startScan") {
      activity?.startActivity(Intent(activity, ScanActivity::class.java))
      result.success(true);
    }
    else if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")

      val scanConfiguration = ScanConfiguration().apply {
        multiPage = true
      }
//      ScanFlow.scanWithConfiguration(activity, scanConfiguration)
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    initWithActivity(binding.activity)
    binding.addActivityResultListener(this)
//    binding.addRequestPermissionsResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    initWithActivity(binding.activity)
    binding.addActivityResultListener(this)
//    binding.addRequestPermissionsResultListener(this)
  }

  fun bundleToMap(extras: Bundle): Map<String, Object?>? {
    val map = mutableMapOf<String, Object?>()
    val ks: Set<String> = extras.keySet()
    val iterator = ks.iterator()
    while (iterator.hasNext()) {
      val key = iterator.next()
      map.put(key, extras.get(key) as Object?)
    }
    return map
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    try {
      if (null != data) {
        Handler(Looper.getMainLooper()).post {
          val extras = data?.extras?.let { bundleToMap(it) }
          _result.success(extras)
        }
      }
    } catch (e: Exception) {
      // There was an error during the scan flow. Check the exception for more details.
      e.printStackTrace()
      println(e.message)
    }
    return true
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ): Boolean {
    val wasPermissionGranted = grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED

    requestingPermission = false;
    return false;
  }

  private fun scanWithConfig(poCall: MethodCall, poResult: Result) {
    ScanSettings.instance.load(poCall)

//    activity?.startActivity(Intent(activity, ScanActivity::class.java))

    activity?.startActivityForResult(Intent(activity, ScanActivity::class.java), REQUEST_CODE);

//    poResult.success(emptyMap<String, Any>())
  }

  private fun generateDocument(poCall: MethodCall, poResult: Result) {
    ScanSettings.instance.load(poCall)

    val pages = DocumentManager.getInstance(context).pages
    val outputFile = File(context.externalCacheDir, ScanSettings.instance.pdfFilename)

    PdfGenerationTask(context, pages, outputFile, ScanSettings.instance.ocr) label@{ isSuccess, error ->
      if (!isSuccess) {
        Toast.makeText(activity, error?.message, Toast.LENGTH_LONG)
          .show()
        return@label
      }

      poResult.success(outputFile)
    }.execute()

    poResult.success(null)
  }

  override fun onCreatedScanner(fragment: ScannerFragment) {
    scanner = fragment
    scanner?.initializeCamera(fragmentMgr)
  }
}
