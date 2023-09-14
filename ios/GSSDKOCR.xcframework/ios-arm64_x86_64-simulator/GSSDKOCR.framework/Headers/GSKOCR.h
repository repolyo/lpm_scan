//
//  GSKOCR.h
//  GSKOCR
//
//  Created by Bruno Virlet on 4/26/19.
//

#import <GSSDKCore/GSSDKCore.h>

NS_ASSUME_NONNULL_BEGIN

///-------------------------
/// @name OCR
///-------------------------

@class GSKPDFTextLayout;

/**
 The result of an OCR operation.
*/
@interface GSKOCRResult: NSObject

- (instancetype)initWithText:(NSString *)text textLayout:(GSKTextLayout *)textLayout;

/** The raw text recognized in the document */
@property (nonatomic, readonly) NSString *text;

/** The text layout of the recognized text */
@property (nonatomic, readonly) GSKTextLayout *textLayout;

@end

/**
 The configuration for an OCR operation
 */
@interface GSKOCRConfiguration : NSObject

/**
 The path to the trained data *folder* (and not file).

 The folder must contain trained data files.
 */
@property (nonatomic, strong) NSString *trainedDataPath;

/**
 The language to process OCR with.

 The corresponding trained data files must be present in the folder specified by trainedDataPath.
 */
@property (nonatomic, strong) NSArray<NSString *> *languageCodes;

@end

/**
 Provides OCR capability for your application.

 @warning The SDK must be initialized with [GSK initWithLicenseKey:error:] first.
 */
@interface GSKOCR : NSObject

/**
 Recognize the text in the provided image

 This call is synchronous an can take some time so it's advised to call it on background thread.

 @param imagePath the path of the image to recognize. Ideally the image has been processed in black and white with [GSDK enhanceImage:withPostProcessing:error:] first. If not, set the `preprocessed` flag on the `configuration` object to NO.
 @param configuration Configuration for the OCR process, in particular the language in which the OCR is performed. Note that you must provided the appropriate trained data.
 @param progressBlock The progress between 0 and 1. Called on the same thread.
 @param error On input, a pointer to an error object. If an error occurs, this pointer is set to an actual error object containing the error information. You may specify nil for this parameter if you do not want the error information.

 @return a GSKOCRResult object containing the result of the text recognition.
 */
+ (GSKOCRResult * _Nullable)recognizeTextForImageAtPath:(NSString *)imagePath
                                       ocrConfiguration:(GSKOCRConfiguration * _Nullable)configuration
                                             onProgress:(void (^)(float))progressBlock
                                                  error:(NSError * _Nullable *)error;

@end

NS_ASSUME_NONNULL_END
