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
@class GSKQuadrangle;

NS_ASSUME_NONNULL_BEGIN

extern const CGFloat GSKEditableFrameDefaultHandleWidth;

/**
 Represents the quadrangle in the quadrangle edition screen.
 */
@protocol GSKEditableFrameDelegate

- (void)editableFrameDidChange:(GSKEditableFrame *)frame;
- (CGRect)editableFrameBoundsForFrameCorners:(GSKEditableFrame *)editableFrame ;
- (void)editableFrame:(GSKEditableFrame *)editableFrame userDidTouchPoint:(CGPoint)currentTouchPosition;
- (void)editableFrame:(GSKEditableFrame *)editableFrame userDidSelectCorner:(CGPoint)cornerPosition withFingerLocation:(CGPoint)pt;
- (void)editableFrameUserDidEndSelectCorner:(GSKEditableFrame *)editableFrame;
- (void)editableFrameUserDidDoubleTap:(GSKEditableFrame *)editableFrame;
@end

@interface GSKEditableFrame : UIView

@property (nonatomic, strong) GSKQuadrangle *quadrangle;

@property (nonatomic, weak, nullable) id <GSKEditableFrameDelegate> delegate;

/**
 The color of the area outside of the quadrangle.
 */
@property (nonatomic, copy) UIColor *outsideShadeColor;

/**
 The color of the area inside of the quadrangle.
 */
@property (nonatomic, copy) UIColor *insideShadeColor;

/**
 The color of the area inside of the quadrangle.
 */
@property (nonatomic, copy) UIColor *shadeColor DEPRECATED_MSG_ATTRIBUTE("Deprecated in favor of `insideShadeColor`.");

/**
 The color of the edges and grid lines.
 */
@property (nonatomic, copy) UIColor *lineColor;

/**
 The width of the handle that signify that corners can be dragged.

 Default: 5
 */
@property (nonatomic, assign) CGFloat handleWidth;
@property (nonatomic, assign) BOOL noCrop;

@end

NS_ASSUME_NONNULL_END
