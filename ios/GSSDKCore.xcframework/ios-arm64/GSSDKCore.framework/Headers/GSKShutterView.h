//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <UIKit/UIKit.h>

/**
 A view to simulate a camera shutter effect.
 */
@interface GSKShutterView : UIView

/**
 Simulate closing the shutter.
 */
- (void)shut;

/**
 Simulate opening the shutter.
 */
- (void)reveal;

@end
