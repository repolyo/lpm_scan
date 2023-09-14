//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//



#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GSKPNGInfo : NSObject

@property (nonatomic, assign) NSUInteger width;
@property (nonatomic, assign) NSUInteger height;
@property (nonatomic, assign) NSUInteger colorType;
@property (nonatomic, assign) NSUInteger bitDepth;

@end

/**
 Returns the PNG header information.
 */
FOUNDATION_EXPORT GSKPNGInfo *pngInfo(NSString *filename);

NS_ASSUME_NONNULL_END
