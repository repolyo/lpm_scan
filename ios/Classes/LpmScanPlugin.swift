import Flutter
import UIKit
import GSSDKCore

public class LpmScanPlugin: NSObject, FlutterPlugin {
  var window: UIWindow?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "lpm_scan", binaryMessenger: registrar.messenger())
    let instance = LpmScanPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)
    case "scanWithConfiguration":
      let arguments = call.arguments as! [String: Any]
      let pdfFilename = arguments["pdfFilename"] as! String
      let fileProvider = arguments["fileProvider"] as? String
      let detectionStatus = arguments["detection_status"] as! String
      let ocr = arguments["ocr"] as? Bool == true
      let configuration = GSKCameraSessionConfiguration()
      let cameraSession = GSKCameraSession(configuration: configuration)

      window = UIWindow(frame: UIScreen.main.bounds)
      let cameraViewController = CameraViewController(cameraSession: cameraSession)!

      let navigationController = UINavigationController(rootViewController: cameraViewController)
      if #available(iOS 13.0, *) {
          let appearance = UINavigationBarAppearance()
          navigationController.navigationBar.standardAppearance = appearance
          navigationController.navigationBar.scrollEdgeAppearance = appearance
      }

      window?.rootViewController = navigationController
      window?.makeKeyAndVisible()

      result("iOS " + UIDevice.current.systemVersion)
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
