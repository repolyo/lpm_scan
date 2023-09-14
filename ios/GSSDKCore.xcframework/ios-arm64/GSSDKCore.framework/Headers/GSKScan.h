//
//  GSKScan.h
//  GSSDK
//
//  Created by Bruno Virlet on 6/2/16.
//
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/**
 A scan, the result of the camera output. It encapsulates a simple file path as well as additional information on the capture.
 */
@interface GSKScan: NSObject

- (instancetype)initWithImage:(UIImage *)image;

/**
 A filepath including the filename to store the unrotated original JPEG out of the camera
*/
@property (nonatomic, strong, readonly) UIImage *image;

@end

NS_ASSUME_NONNULL_END
