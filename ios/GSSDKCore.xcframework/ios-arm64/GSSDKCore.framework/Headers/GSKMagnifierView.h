//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, GSKMagnifierViewCenterStyle) {
    GSKMagnifierViewCenterStyleCrosshair,
    GSKMagnifierViewCenterStyleQuadrangle
};

@class GSKQuadrangle;

@interface GSKMagnifierView : UIView

/**
 The style of the crosshair
 */
@property (nonatomic, assign) GSKMagnifierViewCenterStyle centerStyle;

/**
 Default line width for the crosshair.
 */
@property (nonatomic, assign) CGFloat crosshairLineWidth;

/**
 Default line width for the quadrangle.
 */
@property (nonatomic, assign) CGFloat quadrangleLineWidth;


/**
 The color of the quadrangle crosshair.

 This only applies when the crosshair is of style GSKMagnifierCrosshairStyleQuadrangle.
 */
@property (nonatomic, copy) UIColor *quadrangleColor;

- (void)setSourceImage:(UIImage *)sourceImage;
- (void)setSourceImagePosition:(CGPoint)position quadrangle:(GSKQuadrangle *)quadrangle;

@end
