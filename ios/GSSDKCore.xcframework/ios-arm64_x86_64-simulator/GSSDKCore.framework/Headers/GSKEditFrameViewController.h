//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <UIKit/UIKit.h>

@class GSKEditFrameView;
@class GSKQuadrangle;

NS_ASSUME_NONNULL_BEGIN

/**
 The GSKEditFrameViewController lets the user change a quadrangle.
 
 The quadrangle is a drawn as an overlay over an image. This typically lets the user edit the edges 
 of a document to crop it more accurately.
 */
@interface GSKEditFrameViewController : UIViewController

/**
 @param image the image on which to draw the quadrangle. This is typically your original photo from the camera.
 @param quadrangle the initial quadrangle
 */
- (instancetype)initWithImage:(UIImage *)image quadrangle:(GSKQuadrangle *)quadrangle NS_DESIGNATED_INITIALIZER;

- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithCoder:(NSCoder *)aDecoder NS_UNAVAILABLE;
- (instancetype)initWithNibName:(NSString * _Nullable)nibNameOrNil bundle:(NSBundle * _Nullable)nibBundleOrNil NS_UNAVAILABLE;

/**
 The image on which the quadrangle is overlaid.
 */
@property (nonatomic, strong) UIImage *image;

/**
 Used to set the quadrangle to display in the view controller, and to retrieve the new quadrangle edited by the user.
 */
@property (nonatomic, assign) GSKQuadrangle *quadrangle;

/**
 The view on which the quadrangle is drawn.
 */
@property (nonatomic, readonly) GSKEditFrameView *frameView;

/**
 Customize the color of the shade within the quadrangle.
 */
@property (nonatomic, copy) UIColor *shadeColor DEPRECATED_MSG_ATTRIBUTE("Deprecated in favor of frameView.imageSelection.insideShadeColor].");

/**
 Customize the color of the line used to draw the quadrangle.
 */
@property (nonatomic, copy) UIColor *lineColor;

@end

NS_ASSUME_NONNULL_END
