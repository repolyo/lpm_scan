//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <Foundation/Foundation.h>

@class GSKQuadrangle;
@class GSKTriggerDecider;
@class GSKQuadrangleStreamAnalyzer;

@protocol GSKTriggerDeciderDelegate <NSObject>

- (void)triggerDecider:(GSKTriggerDecider *)triggerDecider decidedToTriggerWithQuadrangle:(GSKQuadrangle *)quadrangle;
- (void)triggerDecider:(GSKTriggerDecider *)triggerDecider suggestedQuadrangle:(GSKQuadrangle *)quadrangle;

- (void)triggerDeciderIsAboutDecide:(GSKTriggerDecider *)triggerDecider;
- (void)triggerDeciderIsSearching:(GSKTriggerDecider *)triggerDecider;

@end

/**
 An object to determine when there is enough stability to trigger a photo.
 */
@interface GSKTriggerDecider : NSObject

/**
 The duration during which the user needs to stay stable before a photo is triggered.

 This is a read-only property; it's exposed so that you can play an animation during that duration.
 In Genius Scan, this corresponds to the ring closing around the shutter button.
 */
+ (NSTimeInterval)minDurationInAboutToTriggerForTrigger;

- (instancetype)init;
- (instancetype)initWithAnalyzer:(GSKQuadrangleStreamAnalyzer *)analyzer NS_DESIGNATED_INITIALIZER;

- (void)reset;
- (void)feedQuadrangle:(GSKQuadrangle *)quadrangle;

@property (nonatomic, weak) id <GSKTriggerDeciderDelegate> delegate;

@end
