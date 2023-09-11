package com.lexmark.lpm_scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import com.geniusscansdk.scanflow.ScanConfiguration
import com.geniusscansdk.scanflow.ScanFlow
import com.lexmark.lpm_scan.camera.ScanActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


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
    val intent = Intent(activity, ScanActivity::class.java);
    intent.putExtra("detection_status", detectionStatus)

    activity?.startActivity(intent)
    poResult.success(emptyMap<String, Object>())
  }

  private fun generateDocument(poCall: MethodCall, poResult: Result) {
    // TODO: implementation
    poResult.success(emptyMap<String, Object>())
  }
}
