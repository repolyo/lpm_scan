//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <UIKit/UIKit.h>

@class GSKEditableFrame;
@class GSKMagnifierView;
@class GSKQuadrangle;

NS_ASSUME_NONNULL_BEGIN

/**
 The view to edit the quadrangle to edit the perspective correction.

 It displays an editable quadrangle overlaid on the original image.
 */
@interface GSKEditFrameView : UIView

- (void)clearSelection;

/**
 The overlay representing the quadrangle.

 In particular, this lets you customize the colors used for the frame overlay.
 */
@property (nonatomic, readonly) GSKEditableFrame *imageSelection;

/**
 The magnifier "loupe" that displays a magnified view of the quadrangle corner being moved.
 */
@property (nonatomic, readonly) GSKMagnifierView *magnifierView;

// Normalized quadrangle
@property (nonatomic, strong) GSKQuadrangle *quadrangle;

/**
 Sets the quadrangle on the view.

 The quadrangle must be in normalized coordinates.

 @param animated If true, the new quadrangle will fade in.
 */
- (void)setQuadrangle:(GSKQuadrangle *)quadrangle animated:(BOOL)animated;

@property (nonatomic, assign) BOOL noCrop;

/**
 Set the image to be displayed by the frame view.

 The frame view will resize the image to reduce memory usage.
 */
- (void)setImage:(UIImage *)image;

/**
 Inset for the image.

 By default this inset takes in account the `imageSelection`'s `handleWidth` to make sure the handles are visible even when selection is maximized.
 */
@property (nonatomic, assign) CGFloat inset;

@end

NS_ASSUME_NONNULL_END
