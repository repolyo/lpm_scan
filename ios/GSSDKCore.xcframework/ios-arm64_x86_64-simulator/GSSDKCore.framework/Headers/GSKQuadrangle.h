//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>

#import <UIKit/UIKit.h>

extern const CGPoint GSKQuadrangleTopRightPoint;
extern const CGPoint GSKQuadrangleTopLeftPoint;
extern const CGPoint GSKQuadrangleBottomLeftPoint;
extern const CGPoint GSKQuadrangleBottomRightPoint;

NS_ASSUME_NONNULL_BEGIN

/**
 Represents a quadrangular area of the photo, generally the document for which to correct
 the perspective.
 
 A quadrangle should always be expressed in the coordinates of the "up" image
 
 A normalized quadrangle: corners expressed in fraction of the image dimensions. Each coordinate will be a floating point
 between 0 and 1.
 */
@interface GSKQuadrangle : NSObject

/// Use one of the static constructors.
- (instancetype)init NS_UNAVAILABLE;

/// Returns an empty quadrangle, where the four points are equal to origin of the coordinates (0, 0).
///
/// This quadrangle has generally no concrete use, but can be used as a marker for an invalid or missing quadrangle.
+ (GSKQuadrangle *)emptyQuadrangle;

/// Builds a full quadrangle. See isFull.
+ (GSKQuadrangle *)fullQuadrangle;

/// Converts rect into a GSKQuadrangle instance.
+ (GSKQuadrangle *)quadrangleFromCGRect:(CGRect)rect;

+ (GSKQuadrangle *)quadrangleWithTopLeft:(CGPoint)topLeft topRight:(CGPoint)topRight bottomLeft:(CGPoint)bottomLeft bottomRight:(CGPoint)bottomRight;

- (BOOL)isEmpty;

/// Returns YES if the quadrangle encompasses the entire image, NO otherwise.
///
/// Note that it's only valid to call this method on a normalized quadrangle.
- (BOOL)isFull;

/// Returns YES if the quadrangle is convex, NO if it's concave.
/// See: https://en.wikipedia.org/wiki/Convex_polygon
- (BOOL)isConvex;

/// The top left point of the quadrangle
@property (nonatomic, readonly) CGPoint topLeft;
/// The bottom top right point of the quadrangle
@property (nonatomic, readonly) CGPoint topRight;
/// The bottom left point of the quadrangle
@property (nonatomic, readonly) CGPoint bottomLeft;
/// The bottom right point of the quadrangle
@property (nonatomic, readonly) CGPoint bottomRight;

/**
 Given a normalized Quadrangle, returns a new quadrangle expressed in the given
 size coordinates
 */
- (GSKQuadrangle *)scaleForSize:(CGSize)size;

/**
 Given a quadrangle expressed in the given size coordinates, returns a new quadrangle
 with normalized coordinates.
 */
- (GSKQuadrangle *)normalizedWithSize:(CGSize)size;

/**
 Given a quadrangle detected on a oriented image, this returns the quadrangle
 that would have been detected if once the image was rotated according to its orientation.
 
 IMPORTANT: this should only be applied to a normalized quadrangle
 */
- (GSKQuadrangle *)rotatedForUpOrientation:(UIImageOrientation)orientation;

/**
 Converts the quadrangle back to not take in account an image orientation
 */
- (GSKQuadrangle *)rotatedWithoutOrientation:(UIImageOrientation)orientation;

/**
 Ensures corners have their coordinates within [0, 1]
 */
- (GSKQuadrangle *)sanitized;
@end

NS_ASSUME_NONNULL_END
