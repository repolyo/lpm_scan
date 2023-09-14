//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#ifndef GSKError_h
#define GSKError_h

/**
 Error handling
 */
FOUNDATION_EXPORT NSString * const GSKErrorDomain;
typedef NS_ENUM(NSInteger, GSKError) {
    /// An license key related error.
    GSKLicenseError = 1000,
    /// An internal error. Please report it to sdk@geniusscan.com with output logs.
    GSKInternalError = 1001,
    /// An image processing error
    GSKProcessingError = 1002,
    /// A general error due to a bad input parameter. Verify that your input match the API requirements.
    GSKBadInputError = 1003,
    /// A file not found error
    GSKFileNotFound = 1004,
    /// A memory (not storage) error
    GSKMemoryError = 1005
};

FOUNDATION_EXPORT NSString * const GSKPDFErrorDomain;

#endif /* GSKError_h */
