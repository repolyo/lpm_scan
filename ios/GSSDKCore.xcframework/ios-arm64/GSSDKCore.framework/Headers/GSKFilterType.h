//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#ifndef GSKFilterType_h
#define GSKFilterType_h

/**
 The different possible enhancements
 */
typedef NS_ENUM(NSInteger, GSKFilterType) {
    /// No post processing
    GSKFilterNone = 0,
    /**
     The black and white enhancement results in a mostly black and white image but gray levels
     are used for antialiasing. The background is turned white.
     */
    GSKFilterBlackAndWhite,
    /// Turns the background white but preserves the color.
    GSKFilterColor,
    /// Enhancement to apply to photographs.
    GSKFilterPhoto,
    /// Monochrome enhancement. The resulting image contains only two colors.
    GSKFilterMonochrome,
};


#endif /* GSKFilterType_h */
