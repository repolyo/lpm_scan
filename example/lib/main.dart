import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lpm_scan/lpm_scan.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _lpmScanPlugin = LpmScan();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _lpmScanPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  initiateScan() async {
    try {
      final scanConfiguration = {
        'source': 'camera',
        // 'multiPage': true,
        // 'ocrConfiguration': {
        //   'languages': ['eng'],
        //   'languagesDirectoryUrl': 'languageFolder.path'
        // },
        'scan_button_text': 'Scan Me',
        'detection_status': 'Align tanch to fit screen...',
        'pdfFilename': 'sample.pdf',
      };

      var scanResult = await _lpmScanPlugin.scanWithConfiguration(
          configuration: scanConfiguration);

      // Here is how you can display the resulting document:
      String documentUrl = scanResult?['multiPageDocumentUrl'];
      // await OpenFile.open(documentUrl.replaceAll("file://", ''));

      // You can also generate your document separately from selected pages:
      /*
          var appFolder = await getApplicationDocumentsDirectory();
          var documentUrl = appFolder.path + '/mydocument.pdf';
          var document = {
            'pages': [{
              'imageUrl': scanResult['scans'][0]['enhancedUrl'] ,
              'hocrTextLayout': scanResult['scans'][0]['ocrResult']['hocrTextLayout']
            }]
          };
          var documentGenerationConfiguration = { 'outputFileUrl': documentUrl };
          await FlutterGeniusScan.generateDocument(document, documentGenerationConfiguration);
          await OpenFile.open(documentUrl);
          */
    } on PlatformException catch (error) {
      displayError(context, error);
    }
  }

  void displayError(BuildContext context, PlatformException error) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(error.message!)));
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Running on: $_platformVersion\n'),
              ElevatedButton(
                onPressed: initiateScan,
                child: const Text('Start Scan'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
