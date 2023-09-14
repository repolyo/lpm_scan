//
// Genius Scan SDK
//
// Copyright 2010-2023 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//



#import <GSSDKCore/GSSDKCore.h>

NS_ASSUME_NONNULL_BEGIN

@interface GSKPDFGeneratorConfiguration (Init)

/**
 A configuration with the specified font path. This configuration will generate a PDF/A-1 with a default ICC profile.
 If you don't want a PDF/A-1, use `initWithFontPath:iccProfilePath:` and pass `nil` for the ICC profile path.
 */
- (instancetype)initWithFontPath:(NSString * _Nullable)fontPath;

@end

NS_ASSUME_NONNULL_END
