import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'lpm_scan_platform_interface.dart';

/// An implementation of [LpmScanPlatform] that uses method channels.
class MethodChannelLpmScan extends LpmScanPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('lpm_scan');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool?> startScan() async {
    return await methodChannel.invokeMethod<bool>('startScan');
  }
}
