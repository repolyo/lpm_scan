
import 'lpm_scan_platform_interface.dart';

class LpmScan {
  Future<String?> getPlatformVersion() {
    return LpmScanPlatform.instance.getPlatformVersion();
  }
}
