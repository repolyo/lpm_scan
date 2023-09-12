import 'lpm_scan_platform_interface.dart';

class LpmScan {
  static const Map<String, dynamic> defaultConfig = {};

  Future<String?> getPlatformVersion() {
    return LpmScanPlatform.instance.getPlatformVersion();
  }

  Future<bool?> startScan() {
    return LpmScanPlatform.instance.startScan();
  }

  Future<Map<String, dynamic>?> scanWithConfiguration(
      {Map<String, dynamic> configuration = defaultConfig}) {
    return LpmScanPlatform.instance.scanWithConfiguration(configuration);
  }

  Future<String?> generateDocument(
      {Map<String, dynamic> configuration = defaultConfig}) {
    // var documentGenerationConfiguration = { 'outputFileUrl': documentUrl };
    return LpmScanPlatform.instance.generateDocument(configuration);
  }
}
