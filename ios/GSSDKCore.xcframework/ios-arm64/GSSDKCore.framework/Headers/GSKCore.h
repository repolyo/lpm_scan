//
// Genius Scan SDK
//
// Copyright 2010-2019 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

#import <CoreMedia/CoreMedia.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class GSKPDFDocument;
@class GSKPDFGeneratorConfiguration;
@class GSKTextLayout;

@protocol GSKPDFImageProcessor;
@protocol GSKLogger;

/**
 Entry point of the SDK. Use this to initialize the SDK with your license key.
*/
@interface GSK : NSObject

/**
 Must be called first with a valide license key to enable
 the SDK.

 @warning *Important* This method must be called before any use of the SDK methods.

 All the SDK errors return proper errors in that case and you can handle them to ensure you provide a good "degraded" experience. For instance, you can prompt the user to update the application to use the scanning feature in case they use a version of the application with an expired license key.

 @param licenseKey The license key
 @param error On input, a pointer to an error object. If an error occurs, this pointer is set to an actual error object containing the error information. You may specify nil for this parameter if you do not want the error information.

 @return YES if the SDK was initialized successfully, NO if an error occured.
 */
+ (BOOL)initWithLicenseKey:(NSString *)licenseKey error:(NSError * _Nullable *)error;

@end

/**
 GSKPDF encapsulate the generation of a PDF document.
 */
@interface GSKPDF : NSObject

/**
 Write the given PDF document to the specified file path.

 @param document The PDF document.
 @param filePath The file path to write the PDF to.
 @param processor An object to preprocess each image before creating the corresponding page. For instance, you can inject a preprocessor to downsample the images.
 @param error On input, a pointer to an error object. If an error occurs, this pointer is set to an actual error object containing the error information. You may specify nil for this parameter if you do not want the error information.

 @return YES if the PDF was generated successfully, NO if an error occured.

 This method will take some time, proportionally to the number of pages of the PDF document.
 */
+ (BOOL)generatePDFDocument:(GSKPDFDocument *)document
                     toPath:(NSString *)filePath
          withConfiguration:(GSKPDFGeneratorConfiguration *)configuration
         withImageProcessor:(_Nullable id<GSKPDFImageProcessor>)processor
                      error:(NSError * _Nullable *)error NS_SWIFT_NAME(generate(_:toPath:configuration:imageProcessor:)) __deprecated_msg("Use GSKDocumentGenerator instead.");

/**
 A convenience shorthand for the previous method with a processor that doesn't apply any change to the images.
 */
+ (BOOL)generatePDFDocument:(GSKPDFDocument *)document
                     toPath:(NSString *)filePath
                      error:(NSError * _Nullable *)error  __deprecated_msg("Use GSKDocumentGenerator instead.");

@end

/**
 GSKTIFF encapsulate the generation of a TIFF document.
 */
@interface GSKTIFF : NSObject

/**
 Write the given TIFF document to the specified file path.

 @param document The TIFF document.
 @param filePath The file path to write the TIFF to.
 @param error On input, a pointer to an error object. If an error occurs, this pointer is set to an actual error object containing the error information. You may specify nil for this parameter if you do not want the error information.

 @return YES if the TIFF was generated successfully, NO if an error occured.

 This method will take some time, proportionally to the number of pages of the TIFF document.
 */
+ (BOOL)generateTIFFDocument:(GSKPDFDocument *)document
                      toPath:(NSString *)filePath
                       error:(NSError * _Nullable *)error;

@end

NS_ASSUME_NONNULL_END
