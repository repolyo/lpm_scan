package com.lexmark.lpm_scan

import android.content.Context
import android.content.Intent
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
class LpmScanPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener  {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var activity: FlutterActivity? = null
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "lpm_scan")
    channel.setMethodCallHandler(this)

  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "scanWithConfiguration") {
      activity?.startActivity(Intent(activity, ScanActivity::class.java))
    }
    else if (call.method == "startScan") {
      activity?.startActivity(Intent(activity, ScanActivity::class.java))
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
    this.activity = binding.activity as FlutterActivity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity as FlutterActivity
    binding.addActivityResultListener(this)
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
}
