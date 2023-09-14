//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#ifndef GSKDetectQuadrangleOptions_h
#define GSKDetectQuadrangleOptions_h

/**
 Quadrangle detection options
 */
typedef NS_OPTIONS(NSInteger, GSKDetectQuadrangleOptions) {
    /// No options
    GSKDetectQuadrangleOptionsNone = 0,
    /// Request a fast quadrangle detection (useful for real-time detection)
    GSKDetectQuadrangleOptionsFast = 1 << 0,
};

#endif /* GSKDetectQuadrangleOptions_h */
