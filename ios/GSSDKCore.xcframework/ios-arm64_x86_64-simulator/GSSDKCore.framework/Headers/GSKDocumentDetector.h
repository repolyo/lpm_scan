//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <CoreMedia/CoreMedia.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class GSKQuadrangleDetectionResult;
@class GSKQuadrangle;

#import <GSSDKCore/GSKDetectQuadrangleOptions.h>

NS_ASSUME_NONNULL_BEGIN

@interface GSKDocumentDetectorConfiguration: NSObject

+ (instancetype)defaultConfiguration;

/**
 A configuration that favors speed over precision.
 */
+ (instancetype)fastConfiguration;

/**
 A configuration that favors precision over speed.
 */
+ (instancetype)highPrecisionConfiguration;

@end

/**
 The result of the quadrangle detection operation.
 */
@interface GSKDocumentDetectionResult: NSObject

- (instancetype)initWithQuadrangle:(GSKQuadrangle *)quadrangle;

/**
 The detected quadrangle or an empty quadrangle if no document has been detected
 */
@property (nonatomic, strong, readonly) GSKQuadrangle *quadrangle;

@end

/**
 - Performance:
    A document detector can be expensive to create: it's recommended to keep a reference to it if it's needed several times in a row.
 */
@interface GSKDocumentDetector : NSObject

- (instancetype)initWithConfiguration:(GSKDocumentDetectorConfiguration *)configuration NS_DESIGNATED_INITIALIZER;
- (instancetype)init;

/**
 Detects the quadrangle corresponding to the edges of a document in video frame

 @param sampleBuffer a YCbCr sample buffer of a video frame. Make sure the video output generating these frames is configured with the pixel format kCVPixelFormatType_420YpCbCr8BiPlanarFullRange
 @param error On input, a pointer to an error object. If an error occurs, this pointer is set to an actual error object containing the error information. You may specify nil for this parameter if you do not want the error information.

 @return The detected quadrangle or nil if an error occured.
 */
- (GSKDocumentDetectionResult * _Nullable)detectDocumentInSampleBuffer:(CMSampleBufferRef)sampleBuffer error:(NSError * _Nullable *)error;

/**
 Detects the quadrangle corresponding to the edges of a document in a photo

 @param image The photo to detect a document in
 @param error On input, a pointer to an error object. If an error occurs, this pointer is set to an actual error object containing the error information. You may specify nil for this parameter if you do not want the error information.

 @return The detected quadrangle or nil if an error occured.
 */
- (GSKDocumentDetectionResult * _Nullable)detectDocumentInImage:(UIImage *)image error:(NSError * _Nullable *)error;

@end

NS_ASSUME_NONNULL_END
