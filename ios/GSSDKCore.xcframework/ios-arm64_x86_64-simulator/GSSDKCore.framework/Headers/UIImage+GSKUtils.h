//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/// Rotation direction for gsk_rotate:
typedef NS_ENUM(NSUInteger, GSKRotationDirection) {
    /// Rotate counter-clockwise
    GSKRotationDirectionLeft,
    /// Rotate clockwise
    GSKRotationDirectionRight,
};

@interface UIImage (GSKUtils)

/**
 Scale an image to fit in a square while preserving its aspect ratio.

 @param maxSize The square to fit the image in. The largest image dimension will fit maxSize.
 @param quality The quality of the interpolation.
 @param scale The scale (density) of the resulting image.
 */
- (UIImage *)gsk_scaleWithMaxSize:(CGFloat)maxSize quality:(CGInterpolationQuality)quality scale:(CGFloat)scale;

/**
 Rotates the image according to its orientation. The resulting image orientation will be "up".
 */
- (UIImage *)gsk_upOrientedImage;

/**
 Rotates the image 90 degrees in the specified direction.

 This will just change the image orientation, so this is very fast, but this works
 because the image orientation is taken in account for display and is written as an EXIF
 orientation by the system methods.

 Should you want to actually rotate the image, you can apply `gsk_upOrientedImage` after rotating it.
 */
- (UIImage *)gsk_rotate:(GSKRotationDirection)direction;

@end

NS_ASSUME_NONNULL_END
