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

#import <GSSDKCore/GSKFilterType.h>
#import <GSSDKCore/GSKDetectQuadrangleOptions.h>

NS_ASSUME_NONNULL_BEGIN

@class GSKQuadrangle;
@class GSKQuadrangleDetectionConfiguration;
@class GSKPerspectiveCorrectionConfiguration;

/// A GSKCurvatureCorrectionConfiguration defines the behavior of a GSKScanProcessor when applying curvature correction.
@interface GSKCurvatureCorrectionConfiguration: NSObject

/// The default configuration. This currently doesn't apply the curvature correction configuration, but this can change in future versions
/// of the SDK.
+ (instancetype)defaultCurvatureCorrectionConfiguration;

/// No curvature correction
+ (instancetype)noCurvatureCorrectionConfiguration;

/// Specifies whether or not you want curvature correction
+ (instancetype)curvatureCorrectionConfigurationWithCurvatureCorrection:(BOOL)curvatureCorrection;

@end

/// A GSKPerspectiveCorrectionConfiguration defines the behavior of a GSKScanProcessor when applying perspective correction.
@interface GSKPerspectiveCorrectionConfiguration: NSObject

/**
 A configuration that will result in the document being auto-detected, and the perspective correction subsequently applied.
 */
+ (instancetype)automaticPerspectiveCorrectionConfiguration;

/**
 A configuration that will result in no perspective correction being applied.
 */
+ (instancetype)noPerspectiveCorrectionConfiguration;

/**
 A configuration that will result in applying the perspective correction defined by the quadrangle.
 */
+ (instancetype)perspectiveCorrectionConfigurationWithQuadrangle:(GSKQuadrangle *)quadrangle;

@end

/// A GSKEnhancementConfiguration defines the behavior of a GSKScanProcessor when applying legibility enhancements.
@interface GSKEnhancementConfiguration: NSObject

/**
 An enhancement configuration that will result in using the best filter, as detected by the Genius Scan SDK. Edge cleaning will be applied.
 */
+ (instancetype)automaticEnhancementConfiguration;

/**
 An enhancement configuration that will result in using the specified filter.
 */
+ (instancetype)enhancementConfigurationWithFilter:(GSKFilterType)filter;

/**
 An enhancement configuration that will result in using the specified filter, applying edge cleaning if requested and if applicable.
 */
+ (instancetype)enhancementConfigurationWithFilter:(GSKFilterType)filter cleanEdges:(BOOL)cleanEdges;

@end

/// Defines an image rotation by increment of 90 degrees.
typedef NS_ENUM(NSInteger, GSKRotation) {
    GSKRotationNone = 0,
    GSKRotationClockwise = 1,
    GSKRotation180 = 2,
    GSKRotationCounterClockwise = 3
};

/// Returns the resulting rotation when applying firstRotation, then secondRotation.
FOUNDATION_EXPORT GSKRotation GSKCombineRotation(GSKRotation firstRotation, GSKRotation secondRotation);
/// Returns the resulting rotation when applying firstRotation, then subtracting secondRotation.
FOUNDATION_EXPORT GSKRotation GSKSubtractRotation(GSKRotation firstRotation, GSKRotation secondRotation);

/// A GSKRotationConfiguration defines the behavior of a GSKScanProcessor when rotating the processed image.
@interface GSKRotationConfiguration: NSObject

/**
 A rotation configuration where no rotation is applied.
 */
+ (instancetype)noRotationConfiguration;

/**
 A rotation configuration where Genius Scan automatically detects the document's orientation.
 */
+ (instancetype)automaticRotationConfiguration;

/**
 A rotation with a specific angle. No automatic rotation is applied.
 */
+ (instancetype)rotationConfigurationWithRotation:(GSKRotation)rotation;

@end

/// A GSKResizeConfiguration defines the behavior of a GSKScanProcessor when resizing the processed image.
@interface GSKResizeConfiguration: NSObject

- (instancetype)initWithMaxDimension:(NSUInteger)maxDimension;

@property (nonatomic, assign, readonly) NSUInteger maxDimension;

@end

/**
 A configuration that defines the output of the processing.
 */
@interface GSKOutputConfiguration: NSObject
/**
    JPEG for color images.
    PNG for monochrome images.
    A default, sensible configuration: jpegQuality: 0.60
 */
+ (instancetype)defaultConfiguration;

/**
    JPEG for color images with the specified quality.
    PNG for monochrome images.
 */
+ (instancetype)automaticConfigurationWithJPEGQuality:(CGFloat)quality;


/// Image output will be JPEG, quality 0.60.
+ (instancetype)jpegOutputConfiguration;

/// Image output will be JPEG with specified quality.
+ (instancetype)jpegOutputConfigurationWithQuality:(CGFloat)quality;

/// Image output will be PNG.
+ (instancetype)pngOutputConfiguration;

@end

@interface GSKProcessingResult: NSObject
/// The output file.
/// It's located in the temporary directory, so you need to move it to a permanent destination.
@property (nonatomic, readonly) NSString *processedImagePath;

/// The quadrangle that was used for perspective correction
@property (nonatomic, strong, readonly) GSKQuadrangle *appliedQuadrangle;

/// The filter that was applied during the enhancement phase
@property (nonatomic, assign, readonly) GSKFilterType appliedFilter;

/**
 The rotation applied during the rotation phase.

 If you specified a rotation angle as part of GSKRotationConfiguration, you will get this angle back here.
 If you requested an automatic orientation detection as part of the GSKRotationConfiguration, appliedRotation will correspond the rotation applied by the SDK
 to rotate the image according to the estimated orientation.

 Note: The output of the processing is always an up-oriented image, even if the original image had an EXIF orientation (see UIImage's imageOrientation property).
 `appliedRotation` doesn't include the rotation applied to the image buffer to remove the EXIF information. The `appliedRotation` only includes the "visual" rotation
 needed to display the image to the user:

 - If the input image imageOrientation is UIImageOrientationUp, and you request a clockwise rotation, appliedRotation will be GSKRotationClockwise.
 - If the input image imageOrientation is UIImageOrientationUp, and you request an automatic rotation, which detects that the image must be rotated clockwise to
  look "straight", appliedRotation will be GSKRotationClockwise.
 - If the input image imageOrientation is UIImageOrientationRight, and you request a clockwise rotation, appliedRotation will be GSKRotationClockwise. The output
  image orientation will be UIImageOrientationUp.
 - If the input image imageOrientation is UIImageOrientationUp, and you request an automatic rotation, which detects that the image must be rotated clockwise to
  look "straight", appliedRotation will be GSKRotationClockwise. The output image orientation will be UIImageOrientationUp.
 */
@property (nonatomic, assign, readonly) GSKRotation appliedRotation;

@end

/// The result of a resizing operation
@interface GSKResizeResult: NSObject

- (instancetype)initWithResizedImagePath:(NSString *)path;

@property (nonatomic, readonly) NSString *resizedImagePath;

@end

// MARK: -

/**
 The configuration object to configure the GSKProcessor's behavior.

 You can use the default constructors.
 */
@interface GSKProcessingConfiguration : NSObject

+ (instancetype)configurationWithPerspectiveCorrectionConfiguration:(GSKPerspectiveCorrectionConfiguration *)perspectiveCorrectionConfiguration
                                   curvatureCorrectionConfiguration:(GSKCurvatureCorrectionConfiguration *)curvatureCorrectionConfiguration
                                           enhancementConfiguration:(GSKEnhancementConfiguration *)enhancementConfiguration
                                              rotationConfiguration:(GSKRotationConfiguration *)rotationConfiguration
                                                outputConfiguration:(GSKOutputConfiguration *)outputConfiguration;

/**
 Automatic perspective correction, distortion correction, followed by automatic enhancement and automatic rotation. Output is JPEG.
 */
+ (instancetype)defaultConfiguration;

/// Specify how to correct perspective distortions present in the scan (such as when the scan was taken with an angle)
@property (nonatomic, strong, readonly) GSKPerspectiveCorrectionConfiguration *perspectiveCorrectionConfiguration;

/// Specify how to correct curvature distortions present in the scan (such as a bent book)
@property (nonatomic, strong, readonly) GSKCurvatureCorrectionConfiguration *curvatureCorrectionConfiguration;

/// The enhancement configuration. This includes the filters enhancing the legibility of the document.
@property (nonatomic, strong, readonly) GSKEnhancementConfiguration *enhancementConfiguration;

/// The rotation configuration. The rotation will be applied after all the other processing.
@property (nonatomic, strong, readonly) GSKRotationConfiguration *rotationConfiguration;

/// Configures the output format of the processing.
@property (nonatomic, strong, readonly) GSKOutputConfiguration *outputConfiguration;

@end

// MARK: -

/**
  The document processor is the central class of the GSSDK's image processing algorithms.

  With the document processor, you can correct the distortion in your documents, as well as improve their legibility.
  If you are only interested in the detecting a document in an image, please refer to GSKDocumentDetector.
*/
@interface GSKScanProcessor : NSObject

/**
 This is the main SDK method and we recommend using this one. By combining multiple operations,
 it can achieve better performance.

 @param configuration The configuration of the different steps of the processing.

 @returns The result of the processing, nil if there is an error. The results includes the parameters that have been selected for the different processing steps,
 as well as the temporary path were the output was written. The output is written in a temporary folder. The caller can take ownership of this file.
 By default, the best output format will be chosen by this method. For instance, if the monochrome enhancement was selected, the output will not
 be saved as JPEG but as a 1-bit PNG image. The result's processedImagePath file extension will reflect this.
 */
- (GSKProcessingResult * _Nullable)processImage:(UIImage *)image
                                  configuration:(GSKProcessingConfiguration *)configuration
                                          error:(NSError **)error;

/**
 Downscale the image at the given path and stores the result in a temporary file.

 Note: this method will never upscale the image. If the image doesn't need downscaling, it will return a copy of the file.
 */
- (GSKResizeResult * _Nullable)resizeImageAtPath:(NSString *)imagePath
                             resizeConfiguration:(GSKResizeConfiguration *)resizeConfiguration
                             outputConfiguration:(GSKOutputConfiguration *)outputConfiguration
                                           error:(NSError * _Nullable *)error;

@end

NS_ASSUME_NONNULL_END
