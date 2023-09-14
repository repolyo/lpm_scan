//
//  GSSDKCore.h
//  GSSDK
//
//  Created by Bruno Virlet on 6/20/15.
//
//

#import <UIKit/UIKit.h>

//! Project version number for GSSDK.
FOUNDATION_EXPORT double GSSDKVersionNumber;

//! Project version string for GSSDK.
FOUNDATION_EXPORT const unsigned char GSSDKVersionString[];

#import <GSSDKCore/GSKCore.h>
#import <GSSDKCore/GSKDocumentDetector.h>
#import <GSSDKCore/GSKScanProcessor.h>
#import <GSSDKCore/GSKPDFImageProcessor.h>
#import <GSSDKCore/GSKError.h>
#import <GSSDKCore/GSKLog.h>
#import <GSSDKCore/GSKDefaultLogger.h>
#import <GSSDKCore/GSKTextLayout.h>
#import <GSSDKCore/GSKTextLayoutToTextConverter.h>
#import <GSSDKCore/GSKTextLayoutToTextConverterResult.h>
#import <GSSDKCore/GSKTextLayoutToTextConverterStatus.h>
#import <GSSDKCore/UIImage+GSKUtils.h>

// UI
#import <GSSDKCore/GSKCameraSession.h>
#import <GSSDKCore/GSKCameraViewController.h>
#import <GSSDKCore/GSKEditableFrame.h>
#import <GSSDKCore/GSKEditFrameView.h>
#import <GSSDKCore/GSKEditFrameViewController.h>
#import <GSSDKCore/GSKShutterView.h>
#import <GSSDKCore/GSKView.h>
#import <GSSDKCore/GSKQuadrangle.h>
#import <GSSDKCore/GSKScan.h>
#import <GSSDKCore/GSKTriggerDecider.h>
#import <GSSDKCore/GSKMagnifierView.h>

// PDF
#import <GSSDKCore/GSKPDFDocument.h>
#import <GSSDKCore/GSKPDFImageProcessor.h>
#import <GSSDKCore/GSKPDFNoopImageProcessor.h>
#import <GSSDKCore/GSKPDFPage.h>
#import <GSSDKCore/GSKPDFSize.h>
#import <GSSDKCore/GSKPDFGeneratorConfiguration.h>
#import <GSSDKCore/GSKPDFGeneratorError.h>
#import <GSSDKCore/GSKPDFGeneratorConfiguration+Init.h>
