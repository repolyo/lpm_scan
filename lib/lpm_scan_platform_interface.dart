import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'lpm_scan_method_channel.dart';

abstract class LpmScanPlatform extends PlatformInterface {
  /// Constructs a LpmScanPlatform.
  LpmScanPlatform() : super(token: _token);

  static final Object _token = Object();

  static LpmScanPlatform _instance = MethodChannelLpmScan();

  /// The default instance of [LpmScanPlatform] to use.
  ///
  /// Defaults to [MethodChannelLpmScan].
  static LpmScanPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [LpmScanPlatform] when
  /// they register themselves.
  static set instance(LpmScanPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> startScan() {
    throw UnimplementedError('startScan() has not been implemented.');
  }

  Future<Map<String, dynamic>?> scanWithConfiguration(
      Map<String, dynamic> configuration) {
    throw UnimplementedError(
        'scanWithConfiguration($configuration) has not been implemented.');
  }
}
