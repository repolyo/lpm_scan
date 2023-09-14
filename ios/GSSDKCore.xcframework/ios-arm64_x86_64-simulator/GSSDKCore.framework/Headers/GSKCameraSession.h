//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <AVFoundation/AVFoundation.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "GSKScan.h"

NS_ASSUME_NONNULL_BEGIN

@class GSKCaptureHandler;
@class GSKQuadrangle;
@class GSKCameraSession;

extern NSString *const kGSKCameraSessionErrorDomain;

typedef NS_ENUM(NSInteger, GSKCameraSessionFlashStatus) {
    GSKCameraSessionFlashStatusAuto,
    GSKCameraSessionFlashStatusOn,
    GSKCameraSessionFlashStatusOff,
};

typedef NS_ENUM(NSInteger, GSKCameraSessionError) {
    GSKCameraSessionErrorOther = -1,
    GSKCameraSessionErrorNotAuthorized = -2,
    GSKCameraSessionErrorLockDevice = -3,
    GSKCameraSessionErrorNoDevice = -4,
    GSKCameraSessionErrorInvalidSetupDependency = -5,
    GSKCameraSessionErrorNoVideoCaptureConnection = -6,
    GSKCameraSessionErrorInvalidData = -7,
    GSKCameraSessionErrorBadInput = -8,

    // Warnings
    GSKCameraSessionWarnLockDevice = -1003,
};

typedef NS_ENUM(NSInteger, GSKCameraSessionDocumentDetection) {
    GSKCameraSessionDocumentDetectionNone,
    GSKCameraSessionDocumentDetectionHighlight,
    GSKCameraSessionDocumentDetectionHighlightAndTrigger,
};

/**
 The delegate of GSKCameraSession must adopt the GSKCameraSessionDelegate protocol.

 This protocol gives information about the state of the camera session, from configuration to snapping of photos.

 There is no guarantee that these callbacks will be called on the main thread.
 */
@protocol GSKCameraSessionDelegate <NSObject>

/**
 The camera session setup finished with an error

 @param cameraSession The camera session
 @param error The error that prevented setup
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession setupFailedWithError:(NSError *)error;

/**
 The camera session encountered a non-fatal error

 @param cameraSession The camera session.
 @param error The encountered error.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didEncounterFailureWithError:(NSError *)error;

/**
 Camera session is going to take the photo

 @param cameraSession The camera session
*/
- (void)cameraSessionWillSnapPhoto:(GSKCameraSession *)cameraSession;

/**
 Camera session just took picture but we haven't post-processed it yet

 @param cameraSession The camera session
 */
- (void)cameraSessionDidSnapPhoto:(GSKCameraSession *)cameraSession;

/**
 The camera session couldn't snap a photo

 @param cameraSession The camera session
 @param error The error that prevented the photo from being taken.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didFailToSnapPhotoWithError:(NSError *)error;

/**
 Camera session has finished processing the photo we just took

 @param cameraSession The camera session
 @param scan The scan object that has been generated. The files will be stored in the directory defined by the GSKCameraSessionOutputConfiguration.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didGenerateScan:(GSKScan *)scan;

/**
 THe camera session failed with an error.

 @param cameraSession The camera session
 @param error The encountered error.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didFailWithError:(NSError *)error;

/**
 The camera session setup finished successfully.

 This is your chance to hook up into
 the setup to change some params on the AVCaptureSession.

 This callback is called on a specific queue where all the session calls are serialized.

 @param cameraSession The camera session
 */
- (void)cameraSessionDidSetup:(GSKCameraSession *)cameraSession;

/**
 The camera session is still looking for a quadrangle.

 This is a general notification about the status of the search: we may or may not have identified
 its edges. If you want to act on identified edges, listen to the `cameraSession:didFindQuadrangle:`
 delegate method.

 @param cameraSession The camera session
 */
- (void)cameraSessionIsSearchingQuadrangle:(GSKCameraSession *)cameraSession;

/**
 The camera session identified a quadrangle in last frame of photo stream

 @param cameraSession The camera session
 @param quadrangle The quadrangle that has been found.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didFindQuadrangle:(GSKQuadrangle *)quadrangle;

/**
 The camera session couldn't identify quadrangle in last frame of photo stream

 @param cameraSession The camera session
 */
- (void)cameraSessionFailedToFindQuadrangle:(GSKCameraSession *)cameraSession;

/**
 The camera session real-time quadrangle detection will soon validate the quadrangle.

 It's a good time to tell the user to remain stable.

 @param cameraSession The camera session
 */
- (void)cameraSessionIsAboutToChooseQuadrangle:(GSKCameraSession *)cameraSession;

/**
 The camera session is going to automatically take the photo

 This callback is called immediately before snapping the photo.

 @param cameraSession The camera session
 @param quadrangle The selected quadrangle
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession willAutoTriggerWithQuadrangle:(GSKQuadrangle *)quadrangle;

/**
 Camera session started running

 @param cameraSession The camera session
 */
- (void)cameraSessionDidStart:(GSKCameraSession *)cameraSession;

/**
 Camera session stopped running

 @param cameraSession The camera session
 */
- (void)cameraSessionDidStop:(GSKCameraSession *)cameraSession;

/**
 Camera session was interrupted.

 This can happen in various cases, including when the user goes to Split View mode

 @param cameraSession The camera session
 @param reason The reason why the camera session was interrupted.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession wasInterruptedWithReason:(AVCaptureSessionInterruptionReason)reason;

/**
 Camera session resumes after interruption

 @param cameraSession The camera session
 */
- (void)cameraSessionInterruptionEnded:(GSKCameraSession *)cameraSession;
@end

/**
 The output configuration for a camera session.
 */
@interface GSKCameraSessionConfiguration: NSObject

/**
 @param documentDetection The type of real-time document detection to apply.
 */
- (instancetype)initWithDocumentDetection:(GSKCameraSessionDocumentDetection)documentDetection NS_DESIGNATED_INITIALIZER;

- (instancetype)init;

/// Default is .highlightAndTrigger
@property (nonatomic, readonly) GSKCameraSessionDocumentDetection documentDetection;

@end

/**
 The GSKCameraSession class manages the interactions with the device camera.
 */
@interface GSKCameraSession : NSObject


- (instancetype)init NS_UNAVAILABLE;

/**
 @param configuration The configuration for the camera session
 */
- (instancetype)initWithConfiguration:(GSKCameraSessionConfiguration *)configuration NS_DESIGNATED_INITIALIZER;

@property (nonatomic, readonly) GSKCaptureHandler *cameraCaptureHandler;

@property (nonatomic, readonly, nullable) AVCaptureDevice *captureDevice;
@property (nonatomic, readonly, nullable) AVCaptureDeviceInput *captureInput;
@property (nonatomic, readonly, nullable) AVCaptureSession *captureSession;
@property (nonatomic, readonly, nullable) AVCaptureConnection *captureConnection;

/**
 Controls when the video session starts and stops delivering photos
 */
- (void)startSessionOnComplete:(void (^)(void))onComplete;
- (void)stopSessionOnComplete:(void (^)(void))onComplete;

- (void)pauseSession;
- (void)resumeSession;

/**
 Sets focus point.
 */
- (void)focusAtPoint:(CGPoint)focusPoint;

/**
 Change the flash status.
 */
@property (nonatomic, assign) GSKCameraSessionFlashStatus flashStatus;

/**
 Returns false if flash isn't supported on this device configuration.
 */
@property (nonatomic, assign) BOOL supportsFlash;

/**
 Manually take a photo

 If autoTriggerEnabled is YES, the photo is taken automatically when the frame is detected.
 Otherwise, if you want to let the user manually trigger the photo, you can use this method.

 This method can be called even when activeDetectionMode is higlightAndTrigger and will force taking the
 photo.

 IMPORTANT: Taking the photo will pause the session. You have to resume the session will `resumeSession` once
 you are done processing the result.
 */
- (void)takePhoto;

/**
 True for the duration of taking the photo and processing it. Observable.
 */
@property (nonatomic, readonly, getter=isTakingPhoto) BOOL takingPhoto;

/**
 Returns the current status of the document detection.

 While you can configure the desired mode when setting up the session, the current value can change: for instance, the
 document detection is disabled while taking the photo.

 Observable.
 */
@property (nonatomic, readonly, assign) GSKCameraSessionDocumentDetection activeDocumentDetection;

/**
Camera won't be used anymore in this session. Makes sure everything can be deallocated successfully.
 */
- (void)cleanup;

/**
 The camera session delegate.

 This must be the cameraViewController.

 @see GSKCameraSessionDelegate
 */
@property (nonatomic, weak, nullable) id <GSKCameraSessionDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
