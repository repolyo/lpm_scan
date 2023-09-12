package com.lexmark.lpm_scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.geniusscansdk.scanflow.ScanConfiguration
import com.geniusscansdk.scanflow.ScanFlow
import com.lexmark.lpm_scan.camera.ScanActivity
import com.lexmark.lpm_scan.enhance.PdfGenerationTask
import com.lexmark.lpm_scan.model.DocumentManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.io.File


/** LpmScanPlugin */
class LpmScanPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener,
  PluginRegistry.RequestPermissionsResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var activity: FlutterActivity? = null
  private lateinit var context: Context

  // Permission request management
  private var requestingPermission = false
  private var permissionRequestResultCallback: Result? = null

  private fun initWithActivity(act: Activity) {
    activity = act as FlutterActivity;
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "lpm_scan")
    channel.setMethodCallHandler(this)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    permissionRequestResultCallback = result;

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

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    try {
      val result = ScanFlow.getScanResultFromActivityResult(data)
      // Do anything with the images or the PDF file
      println(result)
    } catch (e: Exception) {
      // There was an error during the scan flow. Check the exception for more details.
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
    val detectionStatus: String? = poCall.argument("detection_status")
    val pdfFilename: String? = poCall.argument("pdfFilename")
    val fileprovider: String? = poCall.argument("fileprovider")

    val intent = Intent(activity, ScanActivity::class.java);
    intent.putExtra("detection_status", detectionStatus)
    intent.putExtra("pdfFilename", pdfFilename)
    intent.putExtra("fileprovider", fileprovider)

    activity?.startActivity(intent)
//    poResult.success(emptyMap<String, Any>())
  }

  private fun generateDocument(poCall: MethodCall, poResult: Result) {
    var fileprovider: String? = poCall.argument("fileprovider")
    val location: String? = poCall.argument("externalCacheDir")
    val pages = DocumentManager.getInstance(context).pages
    val outputFile = File(context.externalCacheDir, "test.pdf")

    print(location)

    PdfGenerationTask(context, pages, outputFile, true) label@{ isSuccess, error ->
      if (!isSuccess) {
        Toast.makeText(activity, error?.message, Toast.LENGTH_LONG)
          .show()
        return@label
      }

      if (null == fileprovider) {
        fileprovider = BuildConfig.LIBRARY_PACKAGE_NAME
      }

      // View generated PDF document with another compatible installed app
      val uri = FileProvider.getUriForFile(
        activity!!,
        "$fileprovider.fileprovider",
        outputFile
      )
      val intent = Intent(Intent.ACTION_VIEW, uri)
      intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      context.startActivity(intent)
    }.execute()

    poResult.success(emptyMap<String, Object>())
  }
}
