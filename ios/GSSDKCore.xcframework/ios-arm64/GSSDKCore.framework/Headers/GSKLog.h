//
//  GSKLog.h
//  GSSDK
//

#import <Foundation/Foundation.h>

#import "GSKLogger.h"

/**
 An object to control the logging of the Genius Scan SDK.
 */
@interface GSKLog : NSObject

/**
 The SDK logger.

 By default, the SDK will log using NSLog and will not log debug messages.

 Pass nil if you don't want anything logged at all.
 */
@property (class, nonatomic, nullable) id<GSKLogger> logger;

/**
 Log a verbose message directly to the SDK logger.

 This is for the SDK internal use. If you want to customize the logger, inject your own GSKLogger as logger.
 */
+ (void)logVerbose:(NSString * _Nonnull)format, ...;

/**
 Log a debug message directly to the SDK logger.

 This is for the SDK internal use. If you want to customize the logger, inject your own GSKLogger as logger.
 */
+ (void)logDebug:(NSString * _Nonnull)format, ...;

/**
 Log an info message directly to the SDK logger.

 This is for the SDK internal use. If you want to customize the logger, inject your own GSKLogger as logger.
 */
+ (void)logInfo:(NSString * _Nonnull)format, ...;

/**
 Log a warning message directly to the SDK logger.

 This is for the SDK internal use. If you want to customize the logger, inject your own GSKLogger as logger.
 */
+ (void)logWarn:(NSString * _Nonnull)format, ...;

/**
 Log an error message directly to the SDK logger.

 This is for the SDK internal use. If you want to customize the logger, inject your own GSKLogger as logger.
 */
+ (void)logError:(NSString * _Nonnull)format, ...;

@end
