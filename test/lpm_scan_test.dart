import 'package:flutter_test/flutter_test.dart';
import 'package:lpm_scan/lpm_scan.dart';
import 'package:lpm_scan/lpm_scan_platform_interface.dart';
import 'package:lpm_scan/lpm_scan_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockLpmScanPlatform
    with MockPlatformInterfaceMixin
    implements LpmScanPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final LpmScanPlatform initialPlatform = LpmScanPlatform.instance;

  test('$MethodChannelLpmScan is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelLpmScan>());
  });

  test('getPlatformVersion', () async {
    LpmScan lpmScanPlugin = LpmScan();
    MockLpmScanPlatform fakePlatform = MockLpmScanPlatform();
    LpmScanPlatform.instance = fakePlatform;

    expect(await lpmScanPlugin.getPlatformVersion(), '42');
  });
}
